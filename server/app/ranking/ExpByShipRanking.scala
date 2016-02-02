package ranking

import models.db._
import ranking.common.{Ranking, RankingElement}
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
case object ExpByShipRanking extends Ranking {
  import ranking.common.Ranking._

  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")

  override val id = 101
  override val title: String = "合計経験値"
  override val comment: List[String] = List("進化前で集計しています")
  override val divClass: String = colmd3

  override def rankingQuery(limit: Int): Seq[RankingElement] = {
    implicit val session = Ship.autoSession
    val expSum = withSQL {
      select(ms.resultAll, sqls"sum(s.exp) as total").from(Ship as s)
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .groupBy(s.shipId)
        .orderBy(sqls"total").desc
    }.map { rs =>
      rs.int(ms.resultName.id) -> rs.long(sqls"total")
    }.list().apply()
    ShipCommon.toRankingElement(expSum).take(limit)
  }
}
