package com.ponkotuy.intercept

import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import com.ponkotuy.parser.{Query, PostResponse}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
class KCIntercepter extends Intercepter {
  val poster = new PostResponse
  override def input(req: HttpRequest, res: HttpResponse): Unit = {
    Future {
      val q = Query(req, res)
      if(q.parsable) poster.post(q)
    }
  }
}
