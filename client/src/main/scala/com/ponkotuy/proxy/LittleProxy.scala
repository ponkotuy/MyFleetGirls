package com.ponkotuy.proxy

import com.netaporter.uri.Uri
import com.ponkotuy.intercept.Intercepter
import com.ponkotuy.util.ContentResult
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import org.littleshoot.proxy.{HttpFilters, HttpFiltersAdapter, HttpFiltersSourceAdapter}

/**
 *
 * @author ponkotuy
 * Date: 15/03/26.
 */
class LittleProxy(port: Int, inter: Intercepter) {
  val server = DefaultHttpProxyServer.bootstrap()
      .withPort(port)
      .withFiltersSource(new PrintFilter(inter))

  def start(): Unit = {
    server.start()
  }
}

class PrintFilter(inter: Intercepter) extends HttpFiltersSourceAdapter {
  override def filterRequest(orig: HttpRequest, ctx: ChannelHandlerContext): HttpFilters = {
    val builder = Array.newBuilder[Byte]
    new HttpFiltersAdapter(orig, ctx) {
      override def responsePre(obj: HttpObject): HttpObject = {
        ContentResult.fromObject(obj).foreach { result =>
          builder ++= result.content
          if(result.last) inter.input(orig, builder.result(), Uri.parse(orig.getUri))
        }
        obj
      }
    }
  }
}
