package ranking

import controllers.routes
import ranking.common.{RankingElement, Ranking}
import scalikejdbc._
import models.db._
import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
case object ShipBookRanking extends Ranking {
  import Ranking._

  def a = Admiral.a
  lazy val sb = ShipBook.syntax("sb")

  override val title: String = "艦娘図鑑登録"
  override val comment: List[String] = List(comment30days, "図鑑登録は中破絵をカウントに含めています")
  override val divClass: String = colmd3

  override def rankingQuery(limit: Int): List[RankingElement] = {
    findAllOrderByShipBookCount(limit, agoMillis(30.days)).map { case (admiral, count) =>
      val url = routes.UserView.book(admiral.id).toString
      RankingElement(admiral.nickname, <span>{count}</span>, url, count)
    }
  }

  private def findAllOrderByShipBookCount(limit: Int = 10, from: Long = 0L)(
      implicit session: DBSession = ShipBook.autoSession): List[(Admiral, Long)] = {
    val normal = shipBookCountBy(from=from)
    val damaged = shipBookCountBy(sqls"sb.is_dameged = true", from)
    val dCounts: Map[Long, Long] = damaged.map { case (admin, cnt) => admin.id -> cnt }.toMap.withDefaultValue(0L)
    normal.map { case (admin, cnt) =>
      admin -> (cnt + dCounts(admin.id))
    }.sortBy(_._2).reverse.take(limit)
  }

  private[ranking] def shipBookCountBy(where: SQLSyntax = sqls"1", from: Long = 0L)(
    implicit session: DBSession = ShipBook.autoSession): List[(Admiral, Long)] = {
    withSQL {
      select(sb.resultAll, a.resultAll, sqls"count(1) as cnt").from(ShipBook as sb)
        .innerJoin(Admiral as a).on(sb.memberId, a.id)
        .where.gt(sb.updated, from).and.append(where)
        .groupBy(sb.memberId)
    }.map { rs => Admiral(a)(rs) -> rs.long("cnt") }.list().apply()
  }
}
