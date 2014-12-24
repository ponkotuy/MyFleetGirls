package com.ponkotuy.intercept

import com.netaporter.uri.Uri
import org.jboss.netty.handler.codec.http.{HttpResponse, HttpRequest}

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
trait Intercepter {
  def input(req: HttpRequest, res: HttpResponse, uri: Uri): Unit
}
