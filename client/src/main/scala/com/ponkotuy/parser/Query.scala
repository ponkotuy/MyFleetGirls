package com.ponkotuy.parser

import scala.util.Try
import java.nio.charset.Charset
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/21.
 */
case class Query(req: HttpRequest, res: HttpResponse) {
  import Query._
  def uri = req.getUri
  lazy val resType = ResType.fromUri(uri)
  def resCont: String = res.getContent.toString(Charset.forName(UTF8))
  def resJson: Option[JValue] = KCJson.toAst(resCont)
  def reqCont: String = res.getContent.toString(Charset.forName(UTF8))
  def reqMap: Map[String, String] = parseKeyValue(reqCont)
  def parsable: Boolean = resType.isDefined
}

object Query {
  val UTF8 = "UTF-8"
  val Underbar = """\%5F""".r

  def parseKeyValue(str: String): Map[String, String] =
    Try {
      Underbar.replaceAllIn(str, "_").split('&').map {
        elem =>
          val Array(key, value) = elem.split('=')
          key -> value
      }.toMap
    }.getOrElse(Map())
}
