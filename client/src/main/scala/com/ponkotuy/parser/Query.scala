package com.ponkotuy.parser

import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream

import com.netaporter.uri.Uri
import com.ponkotuy.restype.ResType
import com.ponkotuy.tool.PostQueryParser
import com.ponkotuy.util.Log
import io.netty.buffer.{ByteBuf, ByteBufInputStream}
import org.json4s._

import scala.io.{Codec, Source}

/**
 *
 * @author ponkotuy
 * Date: 14/03/21.
 */
case class Query(uri: Uri, requestContent: ByteBuf, responseContent: ByteBuf) {
  def host = uri.host
  lazy val resType = ResType.fromUri(uri.toString())
  def resCont: String = Query.toString(responseContent)
  def resJson: Either[JValue, String] = KCJson.toAst(resCont)
  def reqCont: String = Query.toString(requestContent)
  def reqMap: Map[String, String] = PostQueryParser.parse(reqCont)
  def parsable: Boolean = resType.isDefined
  def req = reqMap
  def obj = resJson match {
    case Left(value) => value
    case Right(message) => JObject()
  }

  def release(): Unit = {
    requestContent.release()
    responseContent.release()
  }
}

object Query extends Log {

  implicit val codec: Codec = Codec(StandardCharsets.UTF_8)

  final val GzipMagic = 0x1f8b

  private def toString(buf: ByteBuf): String =
    if (buf.getUnsignedShort(buf.readerIndex()) == GzipMagic) {
      logger.trace("Decompress gziped stream.");
      val is = new GZIPInputStream(new ByteBufInputStream(buf.duplicate()))
      Source.fromInputStream(is).mkString
    } else {
      buf.toString(StandardCharsets.UTF_8)
    }

}
