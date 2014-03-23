package com.ponkotuy.intercept

import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}
import com.ponkotuy.parser.{Query, PostResponse}

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
class KCIntercepter extends Intercepter {
  val poster = new PostResponse
  override def input(req: HttpRequest, res: HttpResponse): Unit = {
    val q = Query(req, res)
    if(q.parsable) poster.post(q)
  }
}
