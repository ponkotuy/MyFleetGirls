package com.ponkotuy.proxy

import java.net.InetSocketAddress

import com.netaporter.uri.Uri
import com.ponkotuy.config.ClientConfig
import com.ponkotuy.intercept.Intercepter
import com.twitter.conversions.storage._
import com.twitter.conversions.time._
import com.twitter.finagle.builder.{ClientBuilder, ServerBuilder}
import com.twitter.finagle.{ChannelException, Service, http}
import com.twitter.util.{Await, Future}
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse}

import scala.collection.mutable

/** Proxy by Finagle
  *
  * @param port: Server Port.
  * @param inter: com.ponkotuy.intercept.Intercepter
  */
class FinagleProxy(host: String, port: Int, inter: Intercepter) {
  val clients: mutable.Map[String, Service[HttpRequest, HttpResponse]] = mutable.Map()
  val httpProxy = ClientConfig.upstreamProxyHost

  val service = new Service[HttpRequest, HttpResponse] {
    def apply(req: HttpRequest): Future[HttpResponse] = {
      val uri = Uri.parse(req.getUri)
      val res = httpProxy match {
        case Some(httpHost) => client(httpHost.toHostString).apply(req)
        case None =>
          req.setUri(uri.path + uri.queryString)
          client(uri.host.get+ ":80").apply(req)
      }
      res.foreach(rs => inter.input(req, rs, uri))
      res
    }
  }

  val server = try {
    ServerBuilder()
      .codec(http.Http().compressionLevel(0))
      .bindTo(new InetSocketAddress(host,port))
      .name("MyfleetGirlsProxy")
      .build(service)
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
