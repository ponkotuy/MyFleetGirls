package com.ponkotuy.parser

import java.nio.charset.Charset

import com.netaporter.uri.Uri
import io.netty.handler.codec.http.{HttpContent, HttpRequest}
import org.json4s._

import scala.collection.JavaConverters._
import scala.collection.breakOut

/**
 *
 * @author ponkotuy
 * Date: 14/03/21.
 */
case class Query(req: HttpRequest, res: Array[Byte], uri: Uri) extends AutoCloseable {
  def host = uri.host
  def resType = ResType.fromUri(uri.toString())
  lazy val resCont: String = new String(res, Charset.forName("UTF-8"))
  lazy val resJson: Either[JValue, String] = KCJson.toAst(resCont)
  lazy val resBytes: Array[Byte] = res
  def reqMap: Map[String, String] = req.headers().entries().asScala.map(it => it.getKey -> it.getValue)(breakOut)
  def parsable: Boolean = resType.isDefined

  override def close(): Unit = {}
}

object Query {
  val UTF8 = "UTF-8"

  def toString(cont: HttpContent): String = cont.content().toString(Charset.forName("UTF-8"))
}
