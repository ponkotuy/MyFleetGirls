package models.join

import models.db._
import scalikejdbc._

/**
 * Date: 14/06/16.
 */
case class ShipSnapshotWithName(ship: Ship, master: MasterShipBase, stype: MasterStype, spec: MasterShipSpecs, rest: ShipSnapshotRest)
    extends ShipParameter {
  def deckId = rest.deckId
  def num = rest.num
  override lazy val slotMaster: List[MasterSlotItem] = {
    val master = MasterSlotItem.findAll().map(ms => ms.id -> ms).toMap
    ship.slot.map(master.apply)
  }
  override lazy val slotNames: List[String] = slotMaster.map(_.name)
}

case class ShipSnapshotRest(deckId: Long, num: Int)

object ShipSnapshotRest {
  def apply(dss: SyntaxProvider[DeckShipSnapshot])(rs: WrappedResultSet): ShipSnapshotRest = apply(dss.resultName)(rs)
  def apply(dss: ResultName[DeckShipSnapshot])(rs: WrappedResultSet): ShipSnapshotRest = new ShipSnapshotRest(
    rs.get(dss.deckId), rs.get(dss.num)
  )
}
