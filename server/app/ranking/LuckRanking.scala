package ranking

import models.db.{Admiral, Ship}
import scalikejdbc._
import controllers.routes
import scala.concurrent.duration._

/**
 * Date: 14/12/16.
 */
case object LuckRanking extends Ranking {
  import Ranking._

  override val title: String = "運改修度"
  override val comment: List[String] = List(comment7days)
  override val divClass: String = collg3

  override def rankingQuery(limit: Int): List[RankingElement] = {
    val oldest = agoMillis(7.days)
    for {
      ship <- Ship.findAllWithSpec(sqls"s.lucky > (mss.lucky_min + 6) and s.created > ${oldest}").sortBy(-_.upLucky).take(limit)
      admiral <- Admiral.find(ship.memberId)
    } yield {
      RankingElement(admiral.nickname, <span>+{ship.upLucky} <small>{ship.name}{ship.spec.luckyMin}→{ship.lucky}</small></span>, routes.UserView.user(admiral.id).url)
    }
  }
}
