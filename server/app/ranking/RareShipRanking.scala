package ranking

import com.github.nscala_time.time.Imports._
import models.db.ShipBook
import ranking.common.{Ranking, RankingElement}
import scalikejdbc._

/**
 * @author ponkotuy
 * Date: 15/07/28.
 */
object RareShipRanking extends Ranking {
  import Ranking._
  // Titleとして使用
  override def title: String = "レア艦"

  // Rankingを生成するのに使用
  override def rankingQuery(limit: Int): Seq[RankingElement] = {
    val sb = ShipBook.sb
    val admirals = activeAdmiral()
    val books = ShipBook.findAllBy(sqls.in(sb.memberId, admirals))
    val counts = books.groupBy(_.id).filterKeys(EvolutionBase.isBase).mapValues(_.size)
    val max = counts.map(_._2).max
    val notHaveCounts = counts.mapValues { count => (max - count).toLong }.toVector
        .sortBy(-_._2).take(limit)
    ShipCommon.toRankingElement(notHaveCounts)
  }

  // 注釈。同じコメントは1つに纏められます
  override def comment: Seq[String] = List("レア艦はアクティブな提督のうち図鑑にない人の数")

  // Ranking一覧で、Ranking毎のdivのclass設定に使用
  override def divClass: String = colmd3

  private def activeAdmiral()(implicit session: DBSession= ShipBook.autoSession): List[Long] = {
    val sb = ShipBook.sb
    val ago30 = (DateTime.now - 30.days).getMillis
    withSQL {
      select(sqls.distinct(sb.memberId)).from(ShipBook as sb).where.gt(sb.updated, ago30)
    }.map(_.long(1)).list().apply()
  }
}
