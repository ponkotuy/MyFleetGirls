package models.req

import play.api.data.Form
import play.api.data.Forms._

/**
 * @author ponkotuy
 * Date: 15/03/17.
 */
case class SetHonor(memberId: Long, name: String)

object SetHonor {
  val form = Form(
    mapping(
      "userId" -> longNumber(min = 0),
      "name" -> nonEmptyText
    )(SetHonor.apply)(SetHonor.unapply)
  )
}
