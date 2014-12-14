package models.db

import com.ponkotuy.data
import models.join.{SlotItemWithMaster, ShipWithName, ItemWithShip}
import scalikejdbc._
import util.scalikejdbc.BulkInsert._

case class SlotItem(
    memberId: Long,
    id: Int,
    slotitemId: Int,
    name: String,
    locked: Boolean,
    level: Int) {

  def save()(implicit session: DBSession = SlotItem.autoSession): SlotItem = SlotItem.save(this)(session)

  def destroy()(implicit session: DBSession = SlotItem.autoSession): Unit = SlotItem.destroy(this)(session)

  def nameWithLevel: String = {
    val lv = if(level > 0) s"+$level" else ""
    name + lv
  }
}


object SlotItem extends SQLSyntaxSupport[SlotItem] {

  override val tableName = "slot_item"

  override val columns = Seq("member_id", "id", "slotitem_id", "name", "locked", "level")

  def apply(si: SyntaxProvider[SlotItem])(rs: WrappedResultSet): SlotItem = SlotItem(si.resultName)(rs)
  def apply(si: ResultName[SlotItem])(rs: WrappedResultSet): SlotItem = new SlotItem(
    memberId = rs.long(si.memberId),
    id = rs.int(si.id),
    slotitemId = rs.int(si.slotitemId),
    name = rs.string(si.name),
    locked = rs.boolean(si.locked),
    level = rs.int(si.level)
  )

  lazy val si = SlotItem.syntax("si")
  lazy val ssi = ShipSlotItem.syntax("ssi")
  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")
  lazy val mst = MasterStype.syntax("mst")
  lazy val msi = MasterSlotItem.syntax("msi")

  override val autoSession = AutoSession

  def find(id: Int, memberId: Long)(implicit session: DBSession = autoSession): Option[SlotItem] = {
    withSQL {
      select.from(SlotItem as si).where.eq(si.id, id).and.eq(si.memberId, memberId)
    }.map(SlotItem(si.resultName)).single().apply()
  }

  def findIn(xs: Seq[Int], memberId: Long)(implicit session: DBSession = autoSession): Seq[SlotItem] = {
    xs match {
      case Seq() => Nil
      case _ =>
        val result = withSQL {
          select.from(SlotItem as si)
            .where.in(si.id, xs).and.eq(si.memberId, memberId)
        }.map(SlotItem(si.resultName)).list().apply()
        xs.flatMap(id => result.find(_.id == id)) // sort
    }
  }

  def findAll()(implicit session: DBSession = autoSession): List[SlotItem] = {
    withSQL(select.from(SlotItem as si)).map(SlotItem(si.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(SlotItem as si)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[SlotItem] = {
    withSQL {
      select.from(SlotItem as si).where.append(sqls"${where}")
    }.map(SlotItem(si.resultName)).list().apply()
  }

  def findAllArmedShipBy(where: SQLSyntax)(implicit sessin: DBSession = autoSession): List[ShipWithName] = {
    withSQL {
      select.from(SlotItem as si)
        .innerJoin(ShipSlotItem as ssi).on(sqls"${si.id} = ${ssi.slotitemId} and ${si.memberId} = ${ssi.memberId}")
        .leftJoin(Ship as s).on(sqls"${ssi.shipId} = ${s.id} and ${ssi.memberId} = ${s.memberId}")
        .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.append(where)
    }.map { rs =>
      val memberId = rs.long(ssi.resultName.memberId)
      val shipId = rs.int(s.resultName.shipId)
      val slot = Ship.findSlot(memberId, shipId)
      ShipWithName(Ship(s, slot)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
    }.toList().apply()
  }

  def findAllWithArmedShipBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ItemWithShip] = {
    withSQL {
      select.from(SlotItem as si)
        .leftJoin(ShipSlotItem as ssi).on(sqls"${si.id} = ${ssi.slotitemId} and ${si.memberId} = ${ssi.memberId}")
        .leftJoin(Ship as s).on(sqls"${ssi.shipId} = ${s.id} and ${ssi.memberId} = ${s.memberId}")
        .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.append(where)
        .orderBy(si.slotitemId)
    }.map { rs =>
      val slotItem = SlotItem(si)(rs)
      val shipId = rs.intOpt(s.resultName.shipId)
      val slot = shipId.map { id => Ship.findSlot(slotItem.memberId, id) }
      val ship = slot.map { sl => Ship(s, sl)(rs) }
      val withName = ship.map { s =>
        ShipWithName(s, MasterShipBase(ms)(rs), MasterStype(mst)(rs))
      }
      ItemWithShip(slotItem, withName)
    }.toList().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(SlotItem as si).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def countItemBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[(SlotItemWithMaster, Long)] = {
    withSQL {
      select(si.resultAll, msi.resultAll, sqls"count(1) as count").from(SlotItem as si)
        .innerJoin(MasterSlotItem as msi).on(si.slotitemId, msi.id)
        .where.append(sqls"${where}")
        .groupBy(si.slotitemId)
        .orderBy(msi.typ, msi.id)
    }.map { rs =>
      val item = SlotItem(si)(rs)
      val master = MasterSlotItem(msi)(rs)
      SlotItemWithMaster(item, master) -> rs.long("count")
    }.toList().apply()
  }

  def create(
    memberId: Long,
    id: Int,
    slotitemId: Int,
    name: String,
    locked: Boolean = false,
    level: Int = 0)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      insert.into(SlotItem).columns(
        column.memberId,
        column.id,
        column.slotitemId,
        column.name,
        column.locked,
        column.level
      ).values(
          memberId,
          id,
          slotitemId,
          name,
          locked,
          level
        )
    }.update().apply()
  }

  def bulkInsert(xs: Seq[data.SlotItem], memberId: Long)(implicit session: DBSession = autoSession): Unit = {
    val masterNames = MasterSlotItem.findAll().map(msi => msi.id -> msi.name).toMap
    val names = xs.map(x => masterNames(x.slotitemId))
    applyUpdate {
      insert.into(SlotItem)
        .columns(column.memberId, column.id, column.slotitemId, column.name, column.locked, column.level)
        .multiValues(
          Seq.fill(xs.size)(memberId), xs.map(_.id), xs.map(_.slotitemId), names, xs.map(_.locked), xs.map(_.level)
        )
    }
  }

  def save(entity: SlotItem)(implicit session: DBSession = autoSession): SlotItem = {
    withSQL {
      update(SlotItem).set(
        column.memberId -> entity.memberId,
        column.id -> entity.id,
        column.slotitemId -> entity.slotitemId,
        column.name -> entity.name,
        column.locked -> entity.locked,
        column.level -> entity.level
      ).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId)
    }.update().apply()
    entity
  }

  def destroy(entity: SlotItem)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(SlotItem).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId)
    }.update().apply()
  }

  def deleteAllByUser(memberId: Long)(implicit session: DBSession = autoSession): Unit = applyUpdate {
    delete.from(SlotItem).where.eq(column.memberId, memberId)
  }

}

case class MiniItem(id: Int, name: String)
