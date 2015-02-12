package models.join

import models.db.{AreaName, BattleResult}
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/04/23.
 */
case class Stage(area: Int, info: Int) {
  override def toString: String = s"$viewName-$info"

  lazy val areaName = AreaName.find(area)
  def viewName = areaName.map(_.viewName).getOrElse(area.toString)
}

object Stage {
  def apply(br: SyntaxProvider[BattleResult])(rs: WrappedResultSet): Stage =
    new Stage(rs.int(br.areaId), rs.int(br.infoNo))
}
