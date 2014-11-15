package models.join

import models.db.{Admiral, CellInfo, MapRoute}

/**
 * Date: 14/07/15.
 */
case class RouteWithAdmiral(mapRoute: MapRoute, admiral: Admiral) extends Activity {
  def start: Option[CellInfo] = mapRoute.start
  def end: Option[CellInfo] = mapRoute.end
  def areaId: Int = mapRoute.areaId
  def infoNo: Int = mapRoute.infoNo

  override def title: String = "出撃"
  override def message: String = s"${nickname}提督が${areaId}-${infoNo}へ出撃しました"
  override def url: String = controllers.routes.UserView.routeLog(memberId).url
  override def created: Long = mapRoute.created
}
