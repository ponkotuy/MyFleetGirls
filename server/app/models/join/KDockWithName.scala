package models.join

import controllers.routes

/**
 *
 * @author ponkotuy
 * Date: 14/12/15.
 */
case class KDockWithName(
    id: Int,
    memberId: Long,
    completeTime: Long,
    fuel: Int,
    ammo: Int,
    steel: Int,
    bauxite: Int,
    shipId: Int,
    name: String) extends UserActivity {
  override def title: String = "建造"
  override def url: String = routes.UserView.dock(memberId).url
  override def message: String = s"${id}番建造ドックで${name}の建造が完了しました"
  override def key: String = s"${memberId}-${id}"
}
