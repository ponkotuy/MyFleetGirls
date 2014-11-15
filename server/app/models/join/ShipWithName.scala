package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 14/04/15.
 */
case class ShipWithName(ship: Ship, master: MasterShipBase, stype: MasterStype) extends ShipParameter
