package com.ponkotuy.intercept

import com.netaporter.uri.Uri
import com.twitter.finagle.http.{Request, Response}

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
trait Intercepter {
  def input(req: Request, res: Response, uri: Uri): Unit
}
