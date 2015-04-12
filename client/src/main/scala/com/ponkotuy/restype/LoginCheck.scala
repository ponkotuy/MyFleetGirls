package com.ponkotuy.restype

import com.ponkotuy.parser.Query

/**
 *
 * @author ponkotuy
 * Date: 15/04/11.
 */
case object LoginCheck extends ResType {

  override val regexp = "\\A/kcsapi/api_auth_member/logincheck\\z".r

  override def postables(q: Query): Seq[HttpPostable] = Nil
}
