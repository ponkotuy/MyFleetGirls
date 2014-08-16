package com.ponkotuy.parser

import com.github.theon.uri.Uri

import scala.util.Try
import java.net.URLDecoder
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import org.json4s._
import org.jboss.netty.buffer.ChannelBuffer
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream
import scala.io.Source

/**
 *
 * @author ponkotuy
 * Date: 14/03/21.
 */
case class Query(req: HttpRequest, res: HttpResponse, uri: Uri) {
  import Query._
  def host = Uri.parseUri(uri).host
  lazy val resType = ResType.fromUri(uri)
  def resCont: String = Query.toString(res.getContent)
  def resJson: Either[JValue, String] = KCJson.toAst(resCont)
  def reqCont: String = Query.toString(req.getContent)
  def reqMap: Map[String, String] = parseKeyValue(reqCont)
  def parsable: Boolean = resType.isDefined
}

object Query {
  val UTF8 = "UTF-8"

  private def parseKeyValue(str: String): Map[String, String] =
    Try {
      URLDecoder.decode(str, UTF8).split('&').map { elem =>
        val Array(key, value) = elem.split('=')
        key -> value
      }.toMap
    }.getOrElse(Map())

  private def toString(buf: ChannelBuffer): String = {
    val tmp = new Array[Byte](buf.capacity())
    buf.getBytes(0, tmp)
    Try {
      val is = new GZIPInputStream(new ByteArrayInputStream(tmp))
      Source.fromInputStream(is).mkString
    }.getOrElse {
      new String(tmp, UTF8)
    }
  }
}
