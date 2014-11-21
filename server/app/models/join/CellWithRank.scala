package models.join

import models.db.{CellInfo, BattleResult}
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/05/06.
 */
case class CellWithRank(area: Int, info: Int, cell: Int, rank: String, alpha: Option[String])

object CellWithRank {
  def apply(br: SyntaxProvider[BattleResult], ci: SyntaxProvider[CellInfo])(rs: WrappedResultSet): CellWithRank = new CellWithRank(
    rs.int(br.areaId),
    rs.int(br.infoNo),
    rs.int(br.cell),
    rs.string(br.winRank),
    rs.stringOpt(ci.alphabet)
  )
}
