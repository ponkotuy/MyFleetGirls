package ranking

import models.db.YomeShip
import org.json4s.JValue
import ranking.common.{RankingData, Ranking, RankingElement}
import ranking.data.Count

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

  // JSONになったRankingDataをdeserializeする
  override def decodeData(v: JValue): Option[RankingData] = Count.decode(v)
}
