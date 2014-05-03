package com.ponkotuy.proxy

import com.github.theon.uri.Uri
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.{ChannelException, http, Service, Http}
import com.twitter.util.{Future, Await}
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpResponse, HttpRequest}
import com.ponkotuy.intercept.{Intercepter, PassThrough}
import com.twitter.conversions.storage._
import com.twitter.conversions.time._

/** Proxy by Finagle
  *
  * @param hosts: Hosts. Require Port Number(hostname:80 etc...)
  * @param port: Server Port.
  * @param inter: com.ponkotuy.intercept.Intercepter
  */
class FinagleProxy(hosts: String, port: Int, inter: Intercepter = new PassThrough) {
  val client = ClientBuilder()
    .codec(http.Http().maxRequestSize(128.megabytes).maxResponseSize(128.megabytes))
    .timeout(30.seconds)
    .tcpConnectTimeout(30.seconds)
    .hosts(hosts)
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
  val server = try {
    Http.serve(s":$port", service)
  } catch {
    case e: ChannelException =>
      e.printStackTrace()
      println("多重起動していないかどうか確認してください")
      sys.exit(1)
  }

  def start(): Unit = {
    Await.ready(server)
  }
}
