package models.req

import play.api.data.Form
import play.api.data.Forms._

/**
 * @author ponkotuy
 * Date: 15/05/05.
 */
case class PatchInvisible(memberId: Long, name: String, value: Option[Boolean])

object PatchInvisible {
  val form = Form(
    mapping(
      "userId" -> longNumber(min = 0),
      "name" -> nonEmptyText,
      "value" -> optional(boolean)
    )(PatchInvisible.apply)(PatchInvisible.unapply)
  )
}
