package models.join

import models.db.{MasterStype, MasterShipBase}
import org.json4s.JsonDSL._

/**
 *
 * @author ponkotuy
 * Date: 14/11/15.
 */
case class ShipWithFav(ship: MasterShipBase, stype: MasterStype, favCount: Long) {
  def toJson =
    ("name", ship.name) ~
      ("id", ship.id) ~
      ("stype", stype.name) ~
      ("favCount", favCount)
}

object ShipWithFav {
  def fromWithStype(ship: MasterShipWithStype, favTable: Int => Long): ShipWithFav =
    ShipWithFav(ship.ms, ship.stype, favTable(ship.ms.id))
}
