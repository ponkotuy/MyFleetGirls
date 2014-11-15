package ranking

import controllers.routes
import models.db._
import models.join.ShipWithName
import scalikejdbc._
import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
case object YomeRanking extends Ranking {
  import Ranking._

  def a = Admiral.a
  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")
  lazy val mst = MasterStype.syntax("mst")
  lazy val us = UserSettings.syntax("us")

  override val title: String = "嫁艦Lv"
  override val comment: List[String] = List(comment7days)
  override val divClass: String = collg3

  override def rankingQuery(limit: Int): List[RankingElement] = {
    findAllOrderByYomeExp(limit, agoMillis(7.days)).map { case (admiral, ship) =>
      val url = routes.UserView.top(admiral.id).toString()
      RankingElement(admiral.nickname, toElem(ship), url)
    }
  }

  private def findAllOrderByYomeExp(limit: Int = 10, from: Long = 0L)(
    implicit session: DBSession = Ship.autoSession): List[(Admiral, ShipWithName)] = {
    withSQL {
      select.from(Ship as s)
        .innerJoin(Admiral as a).on(s.memberId, a.id)
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
        .innerJoin(UserSettings as us).on(s.memberId, us.memberId)
        .where.gt(s.created, from).and.eq(us.yome, s.id)
        .orderBy(s.exp).desc
        .limit(limit)
    }.map { rs =>
      Admiral(a)(rs) -> ShipWithName(Ship(s, Nil)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
    }.list().apply()
  }
}
