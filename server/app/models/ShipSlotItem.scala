package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import util.scalikejdbc.BulkInsert._

case class ShipSlotItem(
  memberId: Long,
  shipId: Int,
  id: Int,
  slotitemId: Int) {

  def save()(implicit session: DBSession = ShipSlotItem.autoSession): ShipSlotItem = ShipSlotItem.save(this)(session)

  def destroy()(implicit session: DBSession = ShipSlotItem.autoSession): Unit = ShipSlotItem.destroy(this)(session)

}


object ShipSlotItem extends SQLSyntaxSupport[ShipSlotItem] {

  override val tableName = "ship_slot_item"

  override val columns = Seq("member_id", "ship_id", "id", "slotitem_id")

  def apply(ssi: ResultName[ShipSlotItem])(rs: WrappedResultSet): ShipSlotItem = new ShipSlotItem(
    memberId = rs.long(ssi.memberId),
    shipId = rs.int(ssi.shipId),
    id = rs.int(ssi.id),
    slotitemId = rs.int(ssi.slotitemId)
  )

  val ssi = ShipSlotItem.syntax("ssi")

  override val autoSession = AutoSession

  def find(id: Int, memberId: Long, shipId: Int)(implicit session: DBSession = autoSession): Option[ShipSlotItem] = {
    withSQL {
      select.from(ShipSlotItem as ssi).where.eq(ssi.id, id).and.eq(ssi.memberId, memberId).and.eq(ssi.shipId, shipId)
    }.map(ShipSlotItem(ssi.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ShipSlotItem] = {
    withSQL(select.from(ShipSlotItem as ssi)).map(ShipSlotItem(ssi.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(ShipSlotItem as ssi)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ShipSlotItem] = {
    withSQL {
      select.from(ShipSlotItem as ssi).where.append(sqls"${where}")
        .orderBy(ssi.id)
    }.map(ShipSlotItem(ssi.resultName)).list().apply()
  }

  def findAllInShip(memberId: Long, ships: Seq[Int])(implicit session: DBSession = autoSession): List[ShipSlotItem] = {
    withSQL {
      select.from(ShipSlotItem as ssi).where.in(ssi.shipId, ships).and.eq(ssi.memberId, memberId)
    }.map(ShipSlotItem(ssi.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(ShipSlotItem as ssi).where.append(sqls"${where}")
        .orderBy(ssi.id)
    }.map(_.long(1)).single().apply().get
  }

  def create(
    memberId: Long,
    shipId: Int,
    id: Int,
    slotitemId: Int)(implicit session: DBSession = autoSession): ShipSlotItem = {
    withSQL {
      insert.into(ShipSlotItem).columns(
        column.memberId,
        column.shipId,
        column.id,
        column.slotitemId
      ).values(
          memberId,
          shipId,
          id,
          slotitemId
        )
    }.update().apply()

    ShipSlotItem(
      memberId = memberId,
      shipId = shipId,
      id = id,
      slotitemId = slotitemId)
  }

  def bulkInsert(slot: Seq[Int], memberId: Long, shipId: Int)(implicit session: DBSession = autoSession): Seq[ShipSlotItem] = {
    val filtered = slot.filter(0 <= _)
    applyUpdate {
      insert.into(ShipSlotItem)
        .columns(column.memberId, column.shipId, column.id, column.slotitemId)
        .multiValues(
          Seq.fill(filtered.size)(memberId), Seq.fill(filtered.size)(shipId), (1 to filtered.size).toSeq, filtered
        )
    }
    filtered.zipWithIndex.map { case (s, i) => ShipSlotItem(memberId, shipId, i, s) }
  }

  def bulkInserts(slots: Seq[Seq[Int]], memberId: Long, shipId: Seq[Int])(
      implicit session: DBSession = autoSession): Seq[ShipSlotItem] = {
    val filtered = slots.map(_.filter(0 <= _))
    val shipIds = filtered.zip(shipId).flatMap { case (slot, sid) =>
      Seq.fill(slot.size)(sid)
    }
    val ids = filtered.flatMap(1 to _.size)
    val slotFlat = filtered.flatten
    applyUpdate {
      insert.into(ShipSlotItem)
        .columns(column.memberId, column.shipId, column.id, column.slotitemId)
        .multiValues(Seq.fill(shipIds.size)(memberId), shipIds, ids, slotFlat)
    }
    shipIds.zip(ids).zip(slotFlat).map { case ((sid, id), slot) => ShipSlotItem(memberId, sid, id, slot) }
  }

  def bulkUpserts(slots: Seq[Seq[Int]], memberId: Long, shipId: Seq[Int])(
      implicit session: DBSession = autoSession): Unit = {
    val params = slots.zip(shipId).flatMap { case (slot, sid) =>
      slot.filter(0 <= _).zipWithIndex.map { case (x, i) =>
        Seq(memberId, sid, i + 1, x)
      }
    }
    sql"""replace into ship_slot_item (member_id, ship_id, id, slotitem_id) values (?, ?, ?, ?)"""
      .batch(params:_*).apply()
  }

  def save(entity: ShipSlotItem)(implicit session: DBSession = autoSession): ShipSlotItem = {
    withSQL {
      update(ShipSlotItem).set(
        column.memberId -> entity.memberId,
        column.shipId -> entity.shipId,
        column.id -> entity.id,
        column.slotitemId -> entity.slotitemId
      ).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId).and.eq(column.shipId, entity.shipId)
    }.update().apply()
    entity
  }

  def destroy(entity: ShipSlotItem)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(ShipSlotItem).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId).and.eq(column.shipId, entity.shipId)
    }.update().apply()
  }

  def deleteAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Unit =
    applyUpdate { delete.from(ShipSlotItem).where.append(where) }

}
