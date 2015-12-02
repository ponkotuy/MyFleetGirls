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

import scala.collection.mutable

/** Proxy by Finagle
  *
  * @param port: Server Port.
  * @param inter: com.ponkotuy.intercept.Intercepter
  */
class FinagleProxy(host: String, port: Int, inter: Intercepter) {
  val clients: mutable.Map[String, Service[http.Request, http.Response]] = mutable.Map()
  val httpProxy = ClientConfig.upstreamProxyHost

  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] = {
      val uri = Uri.parse(req.uri)
      val res = httpProxy match {
        case Some(httpHost) => client(httpHost.toHostString).apply(req)
        case None =>
          req.uri = uri.path + uri.queryString
          client(uri.host.get+ ":80").apply(req)
      }
      res.foreach(rs => inter.input(req, rs, uri))
      res
    }
  }

  val server = try {
    ServerBuilder()
      .codec(http.Http.get().compressionLevel(0))
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
          .codec(http.Http.get().maxRequestSize(128.megabytes).maxResponseSize(128.megabytes))
          .tcpConnectTimeout(30.seconds)
          .hosts(host)
          .hostConnectionLimit(4)
          .build()
    })
  }

  def start(): Unit = {
    Await.ready(server)
  }
}
