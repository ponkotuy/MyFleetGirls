package models.join

import models.db.{CreateShip, MasterShipBase}
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/04/20.
 */

case class CreateShipWithName(
    fuel: Int, ammo: Int, steel: Int, bauxite: Int, develop: Int,
    largeFlag: Boolean, created: Long, name: String)

object CreateShipWithName {
  def apply(cs: SyntaxProvider[CreateShip], ms: SyntaxProvider[MasterShipBase])(
    rs: WrappedResultSet): CreateShipWithName =
    new CreateShipWithName(
      rs.int(cs.fuel),
      rs.int(cs.ammo),
      rs.int(cs.steel),
      rs.int(cs.bauxite),
      rs.int(cs.develop),
      rs.boolean(cs.largeFlag),
      rs.long(cs.created),
      rs.string(ms.name)
    )
}

case class CreateShipWithName2(memberId: Long, resultShip: Int, largeFlag: Boolean, created: Long, name: String)

object CreateShipWithName2 {
  def apply(cs: SyntaxProvider[CreateShip], ms: SyntaxProvider[MasterShipBase])(
    rs: WrappedResultSet): CreateShipWithName2 =
    new CreateShipWithName2(
      rs.long(cs.memberId),
      rs.int(cs.resultShip),
      rs.boolean(cs.largeFlag),
      rs.long(cs.created),
      rs.string(ms.name)
    )
}
