package models.join

import models.db._
import tool.AirSuperiorityWithSlot

/**
 *
 * @author ponkotuy
 * Date: 14/12/16.
 */
case class ShipWithSpecs(
    ship: Ship,
    master: MasterShipBase,
    stype: MasterStype,
    spec: MasterShipSpecs) extends ShipParameter with WithSlot with AirSuperiorityWithSlot {
  lazy val slot: Seq[SlotItemWithMaster] = SlotItem.findIn(ship.slot, memberId)
}
