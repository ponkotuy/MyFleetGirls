package models.other

import models.db.DeckShip
import models.join.ShipWithName
import scalikejdbc.{AutoSession, DBSession}
import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 15/03/04.
 */
case class ShipWithCondition(id: Int, name: String, stype: String, cond: Int, rest: Int, deckId: Option[Int])

object ShipWithCondition {
  def fromShip(ship: ShipWithName)(implicit session: DBSession = AutoSession): ShipWithCondition = {
    val now = System.currentTimeMillis()
    val cond = ship.cond + ((now - ship.created) / 3.minutes.toMillis * 3).toInt
    val deckId = DeckShip.findByShipId(ship.memberId, ship.id).map(_.deckId)
    ShipWithCondition(ship.id, ship.name, ship.stName, cond, 49 - cond, deckId)
  }
}
