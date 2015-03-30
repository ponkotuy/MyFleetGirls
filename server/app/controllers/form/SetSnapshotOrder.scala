package controllers.form

import play.api.data.Form
import play.api.data.Forms._

/**
 * Date: 15/03/29.
 */
object SetSnapshotOrder {
  val form = Form(
    mapping(
      "userId" -> longNumber(min = 0),
      "snapId" -> longNumber(min = 0),
      "sortOrder" -> number
    )(SetSnapshotOrder.apply)(SetSnapshotOrder.unapply)
  )
}

case class SetSnapshotOrder(memberId: Long, snapId: Long, sortOrder: Int)
