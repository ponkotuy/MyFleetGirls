package models.join

import models.db._
import tool.AirSuperiorityWithSlot

/**
 *
 * @author ponkotuy
 * Date: 14/04/15.
 */
case class DeckShipWithName(
    ship: Ship,
    master: MasterShipBase,
    stype: MasterStype,
    spec: MasterShipSpecs,
    deck: DeckShip
) extends ShipParameter with WithSlot with AirSuperiorityWithSlot  {
  lazy val slot = SlotItem.findIn(ship.slot, memberId)
  def deckId: Int = deck.deckId
  def num: Int = deck.num
}
