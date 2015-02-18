package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 14/10/26.
 */
case class ItemWithShip(item: SlotItem, master: MasterSlotItem, ship: Option[ShipWithName]) {
  def name: String = master.name + item.withLevel
}
