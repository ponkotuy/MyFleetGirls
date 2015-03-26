package com.ponkotuy.intercept

import com.netaporter.uri.Uri
import io.netty.handler.codec.http.HttpRequest

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
trait Intercepter {
  def input(req: HttpRequest, res: Array[Byte], uri: Uri): Unit
}
