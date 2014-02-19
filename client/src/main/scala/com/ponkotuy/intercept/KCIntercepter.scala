package com.ponkotuy.intercept

import scala.collection.JavaConverters._
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import com.ponkotuy.parser.{KCJson, ResType}
import java.nio.charset.Charset

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
class KCIntercepter extends Intercepter {
  import KCIntercepter._
  override def input(req: HttpRequest, res: HttpResponse): Unit = {
    val restype = ResType.fromUri(req.getUri)
    for {
      typ <- restype
      headers = entries4s(req.headers().entries())
      json <- KCJson.toAst(res.getContent.toString(Charset.forName("UTF-8")))
    } {
      typ.run(headers, json)
    }
  }
}

object KCIntercepter {
  def entries4s[K, V](entries: java.util.List[java.util.Map.Entry[K, V]]): Map[K, V] =
    entries.asScala.map { entry => entry.getKey -> entry.getValue }.toMap
}
