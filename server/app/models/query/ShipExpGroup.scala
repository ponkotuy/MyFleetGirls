package models.query

import models.db.{DeckShip, ShipHistory, Ship}
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/05/27.
 */
sealed abstract class ShipExpGroup(val v: Int) {
  def ships(memberId: Long, period: Long)(implicit session: DBSession = AutoSession): List[Int]
}

object ShipExpGroup {
  case object LvTop extends ShipExpGroup(1) {
    override def ships(memberId: Long, period: Long)(implicit session: DBSession = AutoSession): List[Int] = {
      Ship.findAllByUser(memberId).sortBy(-_.exp).take(5).map(_.id)
    }
  }

  case object IncreaseTop extends ShipExpGroup(2) {
    override def ships(memberId: Long, period: Long)(implicit session: DBSession = AutoSession): List[Int] = {
      val sh = ShipHistory.sh
      val from = System.currentTimeMillis() - period
      val histories = ShipHistory.findAllBy(sqls.eq(sh.memberId, memberId).and.ge(sh.created, from))
      val diffs = histories.groupBy(_.shipId).mapValues { xs =>
        val exps = xs.map(_.exp)
        exps.max - exps.min
      }.toList
      diffs.sortBy { case (_, diff) => -diff }.map(_._1).take(5)
    }
  }

  case object FirstFleet extends ShipExpGroup(11) {
    override def ships(memberId: Long, period: Long)(implicit session: DBSession = AutoSession): List[Int] =
      fleetShips(memberId, 1)
  }
  case object SecondFleet extends ShipExpGroup(12) {
    override def ships(memberId: Long, period: Long)(implicit session: DBSession = AutoSession): List[Int] =
      fleetShips(memberId, 2)
  }
  case object ThirdFleet extends ShipExpGroup(13) {
    override def ships(memberId: Long, period: Long)(implicit session: DBSession = AutoSession): List[Int] =
      fleetShips(memberId, 3)
  }
  case object ForthFleet extends ShipExpGroup(14) {
    override def ships(memberId: Long, period: Long)(implicit session: DBSession = AutoSession): List[Int] =
      fleetShips(memberId, 4)
  }

  private def fleetShips(memberId: Long, deckId: Int)(implicit session: DBSession = AutoSession): List[Int] = {
    val ds = DeckShip.ds
    DeckShip.findAllBy(sqls.eq(ds.memberId, memberId).and.eq(ds.deckId, deckId)).map(_.shipId)
  }

  val values = Vector(LvTop, IncreaseTop, FirstFleet, SecondFleet, ThirdFleet, ForthFleet)

  def find(n: Int): Option[ShipExpGroup] = values.find(_.v == n)
}
