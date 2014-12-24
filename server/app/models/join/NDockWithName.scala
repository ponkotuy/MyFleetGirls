package models.join

import controllers.routes

/**
 *
 * @author ponkotuy
 * Date: 14/12/15.
 */
case class NDockWithName(
    id: Int,
    memberId: Long,
    shipId: Int,
    completeTime: Long,
    created: Long,
    masterShipId: Int,
    name: String) extends UserActivity {
  override def title: String = "入渠"
  override def url: String = routes.UserView.dock(memberId).url
  override def message: String = s"${id}番入渠ドックで${name}の修復が完了しました"
  override def key: String = s"${memberId}-${id}"
}
