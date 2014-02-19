package com.ponkotuy.intercept

import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
class PassThrough extends Intercepter {
  override def input(req: HttpRequest, res: HttpResponse): Unit = {}
}
