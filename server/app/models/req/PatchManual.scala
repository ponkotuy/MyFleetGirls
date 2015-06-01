package models.req

import play.api.data.Form
import play.api.data.Forms._

/**
 * @author ponkotuy
 * Date: 15/06/02.
 */
case class PatchManual(memberId: Long, id: Int, value: Option[Boolean])

object PatchManual {
  val form = Form(
    mapping(
      "userId" -> longNumber(min = 0),
      "id" -> number(min = 0),
      "value" -> optional(boolean)
    )(PatchManual.apply)(PatchManual.unapply)
  )
}
