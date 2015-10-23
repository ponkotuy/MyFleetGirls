package com.ponkotuy.restype

import com.ponkotuy.http.MFGHttp
import com.ponkotuy.parser.Query
import com.twitter.io.Buf

import scala.util.Try
import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object ShipSWF extends ResType {
  override def regexp: Regex = """\A/kcs/resources/swf/ships/[a-z]+\.swf\z""".r

  override def postables(q: Query): Seq[Result] = {
    q.uri.query.param("VERSION").map { ver =>
      parseKey(q.toString).filterNot { q => MFGHttp.existsImage(q, ver.toInt) }.map { key =>
        val swf = allRead(q.response.content)
        FilePostable(s"/swf/ship/$key/$ver", "image", 2, swf, "swf")
      }.toList
    }.getOrElse(Nil)
  }

  private def parseKey(str: String): Option[String] =
    Try {
      val filename = str.split('/').last
      filename.takeWhile(_ != '.')
    }.toOption

  def allRead(buf: Buf): Array[Byte] = {
    val arr = new Array[Byte](buf.length)
    buf.write(arr, 0)
    arr
  }
}
