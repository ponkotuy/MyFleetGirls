package com.ponkotuy.util

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.http.{HttpContent, HttpObject, LastHttpContent}

/**
 *
 * @author ponkotuy
 * Date: 15/03/26.
 */
case class ContentResult(last: Boolean, content: Array[Byte])

object ContentResult {
  def fromObject(obj: HttpObject): Option[ContentResult] = {
    try {
      obj match {
        case res: LastHttpContent =>
          res.retain()
          Some(ContentResult(true, allRead(res.content())))
        case res: HttpContent =>
          res.retain()
          Some(ContentResult(false, allRead(res.content())))
        case _ => None
      }
    } catch {
      case e: Throwable => e.printStackTrace(); None
    }
  }

  def allRead(buf: ByteBuf): Array[Byte] = {
    val bytes = new Array[Byte](buf.readableBytes())
    val idx = buf.readerIndex()
    buf.getBytes(idx, bytes)
    bytes
  }
}
