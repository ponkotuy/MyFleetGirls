package com.ponkotuy.intercept

import com.netaporter.uri.Uri
import io.netty.buffer.ByteBuf

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
trait Intercepter {
  def input(uri: Uri, requestContent: ByteBuf, responseContent: ByteBuf): Unit
}
