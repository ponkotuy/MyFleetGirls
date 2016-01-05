package models.join

import models.db._
import scalikejdbc._

/**
 * Date: 14/06/16.
 */
case class ShipSnapshotWithName(
    ship: Ship,
    master: MasterShipBase,
    stype: MasterStype,
    spec: MasterShipSpecs,
    rest: ShipSnapshotRest
) extends ShipSnapshotParameter {
  def withItem(items: Seq[ItemSnapshot]) = new ShipSnapshotWithItem(
    ship = ship,
    master = master,
    stype = stype,
    spec = spec,
    rest = rest,
    items = items
  )
}

case class ShipSnapshotWithItem(
    ship: Ship,
    master: MasterShipBase,
    stype: MasterStype,
    spec: MasterShipSpecs,
    rest: ShipSnapshotRest,
    items: Seq[ItemSnapshot]
) extends ShipSnapshotParameter {
  override def slotNames: Seq[String] = {
    if(items.isEmpty) super.slotNames
    else {
      items.zip(slotMaster).map { case (snap, mas) =>
        val level = if (snap.level > 0) s"+${snap.level}" else ""
        val alv = snap.alv.fold("") { a => s"(${a})" }
        s"${mas.name}${level}${alv}"
      }
    }
  }
}

trait ShipSnapshotParameter extends ShipParameter {
  def rest: ShipSnapshotRest

  def deckId = rest.deckId
  def num = rest.num
  override lazy val slotMaster: Seq[MasterSlotItem] = {
    val master = MasterSlotItem.findAll().map(ms => ms.id -> ms).toMap
    ship.slot.flatMap(master.get)
  }
  override def slotNames: Seq[String] = slotMaster.map(_.name)
}

case class ShipSnapshotRest(id: Long, deckId: Long, num: Int)

object ShipSnapshotRest {
  def apply(dss: SyntaxProvider[DeckShipSnapshot])(rs: WrappedResultSet): ShipSnapshotRest = apply(dss.resultName)(rs)
  def apply(dss: ResultName[DeckShipSnapshot])(rs: WrappedResultSet): ShipSnapshotRest = new ShipSnapshotRest(
    rs.get(dss.id), rs.get(dss.deckId), rs.get(dss.num)
  )
}
