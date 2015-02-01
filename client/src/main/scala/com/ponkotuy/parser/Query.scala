package com.ponkotuy.parser

import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

import com.netaporter.uri.Uri
import com.ponkotuy.tool.PostQueryParser
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse}
import org.json4s._

import scala.io.Source
import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/03/21.
 */
case class Query(req: HttpRequest, res: HttpResponse, uri: Uri) {
  def host = uri.host
  lazy val resType = ResType.fromUri(uri.toString())
  def resCont: String = Query.toString(res.getContent)
  def resJson: Either[JValue, String] = KCJson.toAst(resCont)
  def reqCont: String = Query.toString(req.getContent)
  def reqMap: Map[String, String] = PostQueryParser.parse(reqCont)
  def parsable: Boolean = resType.isDefined
}

object Query {
  val UTF8 = "UTF-8"

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
