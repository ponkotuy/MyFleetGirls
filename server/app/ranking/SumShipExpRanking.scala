package ranking

import controllers.routes
import models.db._
import org.json4s._
import ranking.common.{Ranking, RankingData, RankingElement}
import ranking.data.Exp
import scalikejdbc._

import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
case object SumShipExpRanking extends Ranking {
  import Ranking._

  def a = Admiral.a
  lazy val s = Ship.syntax("s")

  override val id = 6
  override val title: String = "艦娘合計Exp"
  override val comment: List[String] = List(comment7days)
  override val divClass: String = collg3

  override def rankingQuery(limit: Int): List[RankingElement] = {
    findAllOrderByShipExpSum(limit, agoMillis(7.days)).map { case (admiral, exp) =>
      val url = routes.UserView.ship(admiral.id).toString
      RankingElement(admiral.id, admiral.nickname, Exp(exp), url, exp)
    }
  }

  private def findAllOrderByShipExpSum(limit: Int = 10, from: Long = 0L)(
    implicit session: DBSession = Ship.autoSession): List[(Admiral, Long)] = {
    withSQL {
      select(a.resultAll, sqls"sum(s.exp) as sum").from(Ship as s)
        .innerJoin(Admiral as a).on(s.memberId, a.id)
        .where.gt(s.created, from)
        .groupBy(s.memberId)
        .orderBy(sqls"sum").desc
        .limit(limit)
    }.map { rs =>
      Admiral(a)(rs) -> rs.long("sum")
    }.list().apply()
  }

  override def decodeData(v: JValue): Option[RankingData] = Exp.decode(v)
}
