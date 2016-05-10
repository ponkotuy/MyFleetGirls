package com.ponkotuy.proxy

import io.netty.buffer.{ByteBuf, CompositeByteBuf}
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpContent, HttpObject, HttpRequest, HttpResponse, HttpResponseStatus}
import org.littleshoot.proxy.HttpFiltersAdapter

abstract class AggregateContentFilters(_originalRequest: HttpRequest, _ctx: ChannelHandlerContext)
  extends HttpFiltersAdapter(_originalRequest, _ctx) {

  def finished(requestContent: ByteBuf, responseContent: ByteBuf): Unit

  private[this] var released = false
  private[this] val requestBuf = ctx.alloc().compositeBuffer()
  private[this] val responseBuf = ctx.alloc().compositeBuffer()

  override def proxyToServerRequest(httpObject: HttpObject): HttpResponse = {
    if (!released) {
      addContentTo(requestBuf, httpObject)
    }
    super.proxyToServerRequest(httpObject)
  }

  override def serverToProxyResponse(httpObject: HttpObject): HttpObject = {
    if (!released) {
      addContentTo(responseBuf, httpObject)
    }
    super.serverToProxyResponse(httpObject)
  }

  private def addContentTo(buf: CompositeByteBuf, httpObject: HttpObject): Unit = {
    httpObject match {
      case httpContent: HttpContent =>
        val c = httpContent.content()
        if (c.isReadable) {
          buf.addComponent(c.retain())
          buf.writerIndex(buf.writerIndex() + c.readableBytes())
        }
      case _ =>
    }
  }

  override def proxyToClientResponse(httpObject: HttpObject): HttpObject = {
    httpObject match {
      case res: HttpResponse if res.getStatus != HttpResponseStatus.OK => releaseBuffers()
      case _ =>
    }
    super.proxyToClientResponse(httpObject)
  }

  override def serverToProxyResponseReceived(): Unit = {
    if (!released) {
      try
        finished(requestBuf, responseBuf)
      finally
        releaseBuffers()
    }
  }

  private def releaseBuffers(): Unit = {
    if (!released) {
      released = true
      requestBuf.release()
      responseBuf.release()
    }
  }

}
