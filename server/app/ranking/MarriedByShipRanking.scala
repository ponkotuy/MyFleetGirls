package ranking

import models.db.Ship
import org.json4s._
import ranking.common.{RankingData, Ranking, RankingElement}
import ranking.data.Count
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

  override def decodeData(v: JValue): Option[RankingData] = Count.decode(v)
}
