package ranking

import models.db.{Admiral, Ship}
import ranking.common.{RankingData, ShipMini, RankingElement, Ranking}
import scalikejdbc._
import controllers.routes
import scala.concurrent.duration._

/**
 * Date: 14/12/16.
 */
case object LuckRanking extends Ranking {
  import Ranking._

  override val id = 3
  override val title: String = "運改修度"
  override val comment: List[String] = List(comment7days)
  override val divClass: String = collg3

  override def rankingQuery(limit: Int): List[RankingElement] = {
    val oldest = agoMillis(7.days)
    for {
      ship <- Ship.findAllWithSpec(sqls"s.lucky > (mss.lucky_min + 6) and s.created > ${oldest}").sortBy(-_.upLucky).take(limit)
      admiral <- Admiral.find(ship.memberId)
    } yield {
      RankingElement(
        admiral.id,
        admiral.nickname,
        LuckData(toData(ship), ship.spec.luckyMin, ship.lucky),
        routes.UserView.user(admiral.id).url,
        ship.upLucky
      )
    }
  }
}

case class LuckData(ship: ShipMini, luckMin: Int, luck: Int) extends RankingData
