package dat

import scalikejdbc._
import models.BattleResult

/**
 *
 * @author ponkotuy
 * Date: 14/05/06.
 */
case class CellWithRank(area: Int, info: Int, cell: Int, rank: String)

object CellWithRank {
  def apply(br: SyntaxProvider[BattleResult])(rs: WrappedResultSet): CellWithRank = new CellWithRank(
    rs.int(br.areaId),
    rs.int(br.infoNo),
    rs.int(br.cell),
    rs.string(br.winRank)
  )
}
