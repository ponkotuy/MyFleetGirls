package ranking

import models.db.YomeShip
import ranking.common.{Ranking, RankingElement}

/**
 *
 * @author ponkotuy
 * Date: 15/02/25.
 */
case object YomeByShipRanking extends Ranking {
  import ranking.common.Ranking._

  override val id = 102
  override val title = "嫁数"
  override val comment = List("進化前で集計しています")
  override val divClass = colmd3

  override def rankingQuery(limit: Int): Seq[RankingElement] = {
    val yomeCounts = YomeShip.countAllByShip().map { case (ship, count) => ship.id -> count }
    ShipCommon.toRankingElement(yomeCounts).take(limit)
  }
}
