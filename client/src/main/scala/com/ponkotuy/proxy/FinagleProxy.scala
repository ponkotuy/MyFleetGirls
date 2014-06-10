package com.ponkotuy.proxy

import scala.collection.mutable
import com.github.theon.uri.Uri
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.{ChannelException, http, Service, Http}
import com.twitter.util.{Future, Await}
import com.twitter.conversions.storage._
import com.twitter.conversions.time._
import org.jboss.netty.handler.codec.http.{HttpMethod, HttpResponse, HttpRequest}
import com.ponkotuy.intercept.{Intercepter, PassThrough}

/** Proxy by Finagle
  *
  * @param port: Server Port.
  * @param inter: com.ponkotuy.intercept.Intercepter
  */
class FinagleProxy(port: Int, inter: Intercepter = new PassThrough) {
  val clients: mutable.Map[String, Service[HttpRequest, HttpResponse]] = mutable.Map()

  val service = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest): Future[HttpResponse] = {
      val uri = Uri.parseUri(req.getUri)
      if(req.getMethod == HttpMethod.POST) req.setUri(uri.pathRaw)
      val res = client(uri.host.get + ":80").apply(req)
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

  private def client(host: String) = {
    clients.getOrElseUpdate(host, {
      ClientBuilder()
        .codec(http.Http().maxRequestSize(128.megabytes).maxResponseSize(128.megabytes))
        .timeout(30.seconds)
        .tcpConnectTimeout(30.seconds)
        .hosts(host)
        .hostConnectionLimit(4).build()
    })
  }

  def start(): Unit = {
    Await.ready(server)
  }
}
