package com.ponkotuy.restype

import org.json4s.JValue

/**
 *
 * @author ponkotuy
 * Date: 15/04/11.
 */
case object LoginCheck extends ResType {
  import ResType._

  override val regexp = "\\A/kcsapi/api_auth_member/logincheck\\z".r

  override def postables(req: Req, obj: JValue): Seq[HttpPostable] = Nil
}
