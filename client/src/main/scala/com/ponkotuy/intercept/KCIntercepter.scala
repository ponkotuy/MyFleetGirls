package com.ponkotuy.intercept

import scala.collection.JavaConverters._
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import com.ponkotuy.parser.{PostResponse, KCJson, ResType}
import java.nio.charset.Charset
import com.twitter.util.Try

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
class KCIntercepter extends Intercepter {
  import KCIntercepter._

  val post = new PostResponse
  override def input(req: HttpRequest, res: HttpResponse): Unit = {
    val restype = ResType.fromUri(req.getUri)
    for {
      typ <- restype
      json <- KCJson.toAst(res.getContent.toString(Charset.forName(UTF8)))
    } {
      val reqCont = req.getContent.toString(Charset.forName(UTF8))
      post.parseAndPost(typ, parseKeyValue(reqCont), json)
    }
  }
}

object KCIntercepter {
  val UTF8 = "UTF-8"
  val Underbar = """\%5F""".r

  def entries4s[K, V](entries: java.util.List[java.util.Map.Entry[K, V]]): Map[K, V] =
    entries.asScala.map { entry => entry.getKey -> entry.getValue }.toMap

  def parseKeyValue(str: String): Map[String, String] =
    Try {
      Underbar.replaceAllIn(str, "_").split('&').map { elem =>
        val Array(key, value) = elem.split('=')
        key -> value
      }.toMap
    }.getOrElse(Map())
}
