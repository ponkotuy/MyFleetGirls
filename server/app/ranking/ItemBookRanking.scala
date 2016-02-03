package ranking

import controllers.routes
import org.json4s.JValue
import ranking.common.{RankingData, RankingElement, Ranking}
import ranking.data.Count
import scalikejdbc._
import models.db._
import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
case object ItemBookRanking extends Ranking {
  import Ranking._

  def a = Admiral.a
  lazy val ib = ItemBook.syntax("ib")

  override val id = 7
  override val title: String = "装備図鑑登録"
  override val comment: List[String] = List(comment30days)
  override val divClass: String = colmd3

  override def rankingQuery(limit: Int): List[RankingElement] = {
    findAllOrderByItemBookCount(limit, agoMillis(30.days)).map { case (admiral, count) =>
      val url = routes.UserView.book(admiral.id).toString
      RankingElement(admiral.id, admiral.nickname, Count(count), url, count)
    }
  }

  private def findAllOrderByItemBookCount(limit: Int = 10, from: Long = 0L)(
    implicit session: DBSession = ItemBook.autoSession): List[(Admiral, Long)] = {
    withSQL {
      select(ib.resultAll, a.resultAll, sqls"count(1) as cnt").from(ItemBook as ib)
        .innerJoin(Admiral as a).on(ib.memberId, a.id)
        .where.gt(ib.updated, from)
        .groupBy(ib.memberId)
        .orderBy(sqls"cnt").desc
        .limit(limit)
    }.map { rs => Admiral(a)(rs) -> rs.long("cnt") }.list().apply()
  }

  override def decodeData(v: JValue): Option[RankingData] = Count.decode(v)
}
