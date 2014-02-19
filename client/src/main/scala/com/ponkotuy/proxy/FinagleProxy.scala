package com.ponkotuy.proxy

import com.github.theon.uri.Uri
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.{http, Service, Http}
import com.twitter.util.{Future, Await}
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpResponse, HttpRequest}
import com.ponkotuy.intercept.{Intercepter, PassThrough}

/** Proxy by Finagle
  *
  * @param hosts: Require Port Number(hostname:80 etc...)
  * @param inter: com.ponkotuy.intercept.Intercepter
  */
class FinagleProxy(hosts: String, inter: Intercepter = new PassThrough) {
  val client = ClientBuilder()
    .codec(http.Http())
    .hosts("125.6.189.39:80")
    .hostConnectionLimit(4).build()

  val service = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest): Future[HttpResponse] = {
      val res = if(req.getMethod == HttpMethod.POST) {
        val uri = Uri.parseUri(req.getUri)
        req.setUri(uri.pathRaw)
        client.apply(req)
      } else {
        client.apply(req)
      }
      res.foreach(rs => inter.input(req, rs))
      res
    }
  }
  val server = Http.serve(":8080", service)

  def start(): Unit = {
    Await.ready(server)
  }
}
