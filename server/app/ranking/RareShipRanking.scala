package ranking

import com.github.nscala_time.time.Imports._
import models.db.ShipBook
import org.json4s.JValue
import ranking.common.{RankingData, EvolutionBase, Ranking, RankingElement}
import ranking.data.Count
import scalikejdbc._

/**
 * @author ponkotuy
 * Date: 15/07/28.
 */
object RareShipRanking extends Ranking {
  import Ranking._
  import util.MFGDateUtil._

  override val id = 105
  override val title: String = "レア艦"
  override val comment: Seq[String] = List("レア艦はアクティブな提督のうち図鑑にない人の数")
  override val divClass: String = colmd3

  // Rankingを生成するのに使用
  override def rankingQuery(limit: Int): Seq[RankingElement] = {
    val sb = ShipBook.sb
    val admirals = activeAdmiral()
    val books = ShipBook.findAllBy(sqls.in(sb.memberId, admirals).and.lt(sb.indexNo, 100000))
    val counts = books.groupBy(_.id).filterKeys(EvolutionBase.isBase).mapValues(_.size)
    if (counts.isEmpty) Nil
    else {
      val max = counts.values.max
      val notHaveCounts = counts.mapValues { count => (max - count).toLong }.toVector
        .sortBy(-_._2).take(limit)
      ShipCommon.toRankingElement(notHaveCounts)
    }
  }

  private def activeAdmiral()(implicit session: DBSession= ShipBook.autoSession): List[Long] = {
    val sb = ShipBook.sb
    val ago30 = (DateTime.now(Tokyo) - 30.days).getMillis
    withSQL {
      select(sqls.distinct(sb.memberId)).from(ShipBook as sb).where.gt(sb.updated, ago30)
    }.map(_.long(1)).list().apply()
  }

  // JSONになったRankingDataをdeserializeする
  override def decodeData(v: JValue): Option[RankingData] = Count.decode(v)
}
