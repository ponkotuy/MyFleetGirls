package dat

import scalikejdbc.SQLInterpolation._
import scalikejdbc.WrappedResultSet
import models.BattleResult

/**
 *
 * @author ponkotuy
 * Date: 14/04/23.
 */
case class Stage(area: Int, info: Int) {
  override def toString: String = s"$area-$info"
}

object Stage {
  def apply(br: SyntaxProvider[BattleResult])(rs: WrappedResultSet): Stage =
    new Stage(rs.int(br.areaId), rs.int(br.infoNo))
}
