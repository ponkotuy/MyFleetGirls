package ranking

import controllers.routes
import scalikejdbc._
import models._
import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
case object MarriedRanking extends Ranking {
  import Ranking._

  override val title: String = "ケッコン数（図鑑）"
  override val comment: List[String] = List(comment30days)
  override val divClass: String = colmd3

  override def rankingQuery(limit: Int): List[RankingElement] = {
    findAllOrderByMarriedCount(limit, agoMillis(7.days)).map { case (admiral, count) =>
      val url = routes.UserView.book(admiral.id).toString()
      RankingElement(admiral.nickname, <span>{count}</span>, url)
    }
  }

  private def findAllOrderByMarriedCount(limit: Int = 10, from: Long = 0L)(
      implicit session: DBSession = ShipBook.autoSession): List[(Admiral, Long)] = {
    ShipBookRanking.shipBookCountBy(sqls"sb.is_married = true", from).sortBy(_._2).reverse.take(limit)
  }
}
