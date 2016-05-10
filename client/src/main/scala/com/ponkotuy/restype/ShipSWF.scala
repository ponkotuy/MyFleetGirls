package com.ponkotuy.restype

import com.ponkotuy.http.MFGHttp
import com.ponkotuy.parser.Query
import io.netty.buffer.ByteBuf

import scala.util.Try
import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object ShipSWF extends ResType {
  val DefaultVer = 0

  override def regexp: Regex = """\A/kcs/resources/swf/ships/[a-z]+\.swf\z""".r

  override def postables(q: Query): Seq[Result] = {
    val ver = q.uri.query.param("VERSION").map(_.toInt).getOrElse(DefaultVer)
    parseKey(q.toString).filterNot { q => MFGHttp.existsImage(q, ver) }.map { key =>
      val swf = allRead(q.responseContent)
      FilePostable(s"/swf/ship/$key/$ver", "image", 2, swf, "swf")
    }.toList
  }

  private def parseKey(str: String): Option[String] =
    Try {
      val filename = str.split('/').last
      filename.takeWhile(_ != '.')
    }.toOption

  def allRead(buf: ByteBuf): Array[Byte] = {
    val arr = new Array[Byte](buf.readableBytes())
    buf.getBytes(buf.readerIndex(), arr)
    arr
  }
}
