package models.db

import com.ponkotuy.data
import models.join.{SlotItemWithMaster, ShipWithName, ItemWithShip}
import scalikejdbc._
import util.scalikejdbc.BulkInsert._

/**
 *
 * @param alv 艦載機熟練度 1-7
 * @param created 残念だったな、初期から実装してなかった所為で永遠にnullableだ、HAHAHA!
 */
case class SlotItem(
    memberId: Long,
    id: Int,
    slotitemId: Int,
    locked: Boolean,
    level: Int,
    alv: Option[Int],
    created: Option[Long]
) {

  def save()(implicit session: DBSession = SlotItem.autoSession): SlotItem = SlotItem.save(this)(session)

  def destroy()(implicit session: DBSession = SlotItem.autoSession): Unit = SlotItem.destroy(this)(session)

  def withLevel: String = if(level > 0) s"+$level" else ""
}


object SlotItem extends SQLSyntaxSupport[SlotItem] {

  override val tableName = "slot_item"

  override val columns = Seq("member_id", "id", "slotitem_id", "locked", "level", "alv", "created")

  def apply(si: SyntaxProvider[SlotItem])(rs: WrappedResultSet): SlotItem = SlotItem(si.resultName)(rs)
  def apply(si: ResultName[SlotItem])(rs: WrappedResultSet): SlotItem = autoConstruct(rs, si)

  val si = SlotItem.syntax("si")
  val ssi = ShipSlotItem.syntax("ssi")
  val s = Ship.syntax("s")
  val ms = MasterShipBase.syntax("ms")
  val mst = MasterStype.syntax("mst")
  val msi = MasterSlotItem.syntax("msi")
  val mss = MasterShipSpecs.syntax("mss")

  override val autoSession = AutoSession

  def find(id: Int, memberId: Long)(implicit session: DBSession = autoSession): Option[SlotItem] = {
    withSQL {
      select.from(SlotItem as si).where.eq(si.id, id).and.eq(si.memberId, memberId)
    }.map(SlotItem(si.resultName)).single().apply()
  }

  def findIn(xs: Seq[Int], memberId: Long)(implicit session: DBSession = autoSession): Seq[SlotItemWithMaster] = {
    xs match {
      case Seq() => Nil
      case _ =>
        val result = withSQL {
          select.from(SlotItem as si)
            .innerJoin(MasterSlotItem as msi).on(si.slotitemId, msi.id)
            .where.in(si.id, xs).and.eq(si.memberId, memberId)
        }.map { rs =>
          SlotItemWithMaster(SlotItem(si)(rs), MasterSlotItem(msi)(rs))
        }.list().apply()
        xs.flatMap(id => result.find(_.item.id == id)) // sort
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
        .innerJoin(Ship as s).on(sqls"${ssi.shipId} = ${s.id} and ${ssi.memberId} = ${s.memberId}")
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
        .innerJoin(MasterShipSpecs as mss).on(s.shipId, mss.id)
        .where.append(where)
    }.map { rs =>
      val memberId = rs.long(ssi.resultName.memberId)
      val shipId = rs.int(s.resultName.shipId)
      val slot = Ship.findSlot(memberId, shipId)
      ShipWithName(Ship(s, slot)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs), MasterShipSpecs(mss)(rs))
    }.toList().apply()
  }

  def findAllWithArmedShipBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ItemWithShip] = {
    withSQL {
      select.from(SlotItem as si)
        .innerJoin(MasterSlotItem as msi).on(si.slotitemId, msi.id)
        .leftJoin(ShipSlotItem as ssi).on(sqls.eq(si.id, ssi.slotitemId).and.eq(si.memberId, ssi.memberId))
        .leftJoin(Ship as s).on(sqls.eq(ssi.shipId, s.id).and.eq(ssi.memberId, s.memberId))
        .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .leftJoin(MasterShipSpecs as mss).on(s.shipId, mss.id)
        .where.append(where)
        .orderBy(si.slotitemId)
    }.map { rs =>
      val slotItem = SlotItem(si)(rs)
      val shipId = rs.intOpt(s.resultName.shipId)
      val slot = shipId.map { id => Ship.findSlot(slotItem.memberId, id) }
      val ship = slot.map { sl => Ship(s, sl)(rs) }
      val withName = ship.map { s =>
        ShipWithName(s, MasterShipBase(ms)(rs), MasterStype(mst)(rs), MasterShipSpecs(mss)(rs))
      }
      val master = MasterSlotItem(msi)(rs)
      ItemWithShip(slotItem, master, withName)
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
      locked: Boolean = false,
      level: Int = 0,
      alv: Option[Int] = None,
      created: Long = System.currentTimeMillis())(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      insert.into(SlotItem).columns(
        column.memberId,
        column.id,
        column.slotitemId,
        column.locked,
        column.level,
        column.alv,
        column.created
      ).values(
          memberId,
          id,
          slotitemId,
          locked,
          level,
          alv,
          created
        )
    }.update().apply()
  }

  def bulkInsert(xs: Seq[data.SlotItem], memberId: Long)(implicit session: DBSession = autoSession): Unit = {
    val now = System.currentTimeMillis()
    applyUpdate {
      insert.into(SlotItem)
          .columns(column.memberId, column.id, column.slotitemId, column.locked, column.level, column.alv, column.created)
          .multiValues(
            Seq.fill(xs.size)(memberId),
            xs.map(_.id),
            xs.map(_.slotitemId),
            xs.map(_.locked),
            xs.map(_.level),
            xs.map(_.alv),
            Seq.fill(xs.size)(now)
          )
    }
  }

  def save(entity: SlotItem)(implicit session: DBSession = autoSession): SlotItem = {
    withSQL {
      update(SlotItem).set(
        column.memberId -> entity.memberId,
        column.id -> entity.id,
        column.slotitemId -> entity.slotitemId,
        column.locked -> entity.locked,
        column.level -> entity.level,
        column.alv -> entity.alv,
        column.created -> entity.created
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
