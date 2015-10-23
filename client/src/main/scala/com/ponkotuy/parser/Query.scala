package com.ponkotuy.parser

import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

import com.netaporter.uri.Uri
import com.ponkotuy.restype.ResType
import com.ponkotuy.tool.PostQueryParser
import com.twitter.finagle.http.{Request, Response}
import com.twitter.io.Buf
import org.json4s._

import scala.io.Source
import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/03/21.
 */
case class Query(request: Request, response: Response, uri: Uri) {
  def host = uri.host
  lazy val resType = ResType.fromUri(uri.toString())
  def resCont: String = Query.toString(response.content)
  def resJson: Either[JValue, String] = KCJson.toAst(resCont)
  def reqCont: String = Query.toString(request.content)
  def reqMap: Map[String, String] = PostQueryParser.parse(reqCont)
  def parsable: Boolean = resType.isDefined
  def req = reqMap
  def obj = resJson match {
    case Left(value) => value
    case Right(message) => JObject()
  }
}

object Query {
  val UTF8 = "UTF-8"

  private def toString(buf: Buf): String = {
    val tmp = new Array[Byte](buf.length)
    buf.write(tmp, 0)
    Try {
      val is = new GZIPInputStream(new ByteArrayInputStream(tmp))
      Source.fromInputStream(is).mkString
    }.getOrElse {
      new String(tmp, UTF8)
    }
  }
}
