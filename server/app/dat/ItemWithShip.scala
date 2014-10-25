package dat

import models._

/**
 *
 * @author ponkotuy
 * Date: 14/10/26.
 */
case class ItemWithShip(item: SlotItem, ship: Option[ShipWithName]) {
  def name: String = item.nameWithLevel
}
