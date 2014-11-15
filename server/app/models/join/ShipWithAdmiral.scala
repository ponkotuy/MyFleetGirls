package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 14/10/12.
 */
case class ShipWithAdmiral(ship: Ship, admiral: Admiral) {
  def id = ship.id
  def lv = ship.lv
}
