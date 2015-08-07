package controllers

import play.api.mvc.Results

/**
 * @author ponkotuy
 * Date: 15/05/05.
 */
object Res extends Results {
  val success = Ok("Successs")
  val noChange = Ok("No Change")
  val authFail = Unauthorized("Authentication failure")
}
