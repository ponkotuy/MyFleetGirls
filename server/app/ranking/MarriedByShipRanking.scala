package ranking

import models.db.Ship
import ranking.common.{Ranking, RankingElement}
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/02/27.
 */
case object MarriedByShipRanking extends Ranking {
  import ranking.common.Ranking._

  override val id = 103
  override val title: String = "ケッコン数"
  override val comment: Seq[String] = List("進化前で集計しています")
  override val divClass: String = colmd3

  // Rankingを生成するのに使用
  override def rankingQuery(limit: Int): Seq[RankingElement] = {
    val marrieds = Ship.countAllShip(sqls.ge(Ship.s.lv, 100)).toVector
    ShipCommon.toRankingElement(marrieds).take(limit)
  }
}
