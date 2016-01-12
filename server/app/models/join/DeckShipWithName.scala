package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 14/04/15.
 */
case class DeckShipWithName(ship: Ship, master: MasterShipBase, stype: MasterStype, spec: MasterShipSpecs, deck: DeckShip) extends ShipParameter{
  def deckId: Int = deck.deckId
  def num: Int = deck.num
}
