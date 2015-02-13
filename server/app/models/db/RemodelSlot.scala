package models.db

import com.ponkotuy.data
import models.join.RemodelWithShip
import scalikejdbc._
import util.scalikejdbc.BulkInsert._

import scala.util.Try

case class RemodelSlot(
  id: Int,
  slotId: Int,
  memberId: Long,
  secondShip: Option[Int],
  fuel: Int,
  ammo: Int,
  steel: Int,
  bauxite: Int,
  develop: Int,
  revamping: Int,
  reqSlotId: Int,
  slotNum: Int,
  created: Long) {

  def save()(implicit session: DBSession = RemodelSlot.autoSession): RemodelSlot = RemodelSlot.save(this)(session)

  def destroy()(implicit session: DBSession = RemodelSlot.autoSession): Unit = RemodelSlot.destroy(this)(session)

}


object RemodelSlot extends SQLSyntaxSupport[RemodelSlot] {

  override val tableName = "remodel_slot"

  override val columns = Seq("id", "slot_id", "member_id", "second_ship", "fuel", "ammo", "steel", "bauxite", "develop", "revamping", "req_slot_id", "slot_num", "created")

  def apply(r: SyntaxProvider[RemodelSlot])(rs: WrappedResultSet): RemodelSlot = apply(r.resultName)(rs)
  def apply(r: ResultName[RemodelSlot])(rs: WrappedResultSet): RemodelSlot = autoConstruct(rs, r)

  lazy val r = RemodelSlot.syntax("r")
  lazy val ms = MasterShipBase.syntax("ms")

  override val autoSession = AutoSession

  def findAll()(implicit session: DBSession = autoSession): List[RemodelSlot] = {
    withSQL(select.from(RemodelSlot as r)).map(RemodelSlot(r.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(RemodelSlot as r)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[RemodelSlot] = {
    withSQL {
      select.from(RemodelSlot as r).where.append(sqls"${where}")
    }.map(RemodelSlot(r.resultName)).list().apply()
  }

  def findAllUniqueSlotId()(implicit session: DBSession = autoSession): List[Int] = {
    sql"select distinct ${r.slotId} from ${RemodelSlot as r}"
      .map(_.int(1)).list().apply()
  }

  def findAllWithSecondShipBy(where: SQLSyntax, limit: Int = 20)(implicit session: DBSession = autoSession): List[RemodelWithShip] = {
    withSQL {
      select.from(RemodelSlot as r)
        .leftJoin(MasterShipBase as ms).on(r.secondShip, ms.id)
        .where.append(sqls"${where}")
        .orderBy(r.created.desc)
        .limit(limit)
    }.map { rs =>
      RemodelWithShip(RemodelSlot(r)(rs), Try { MasterShipBase(ms)(rs) }.toOption)
    }.list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(RemodelSlot as r).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    id: Int,
    slotId: Int,
    memberId: Long,
    secondShip: Option[Int],
    fuel: Int,
    ammo: Int,
    steel: Int,
    bauxite: Int,
    develop: Int,
    revamping: Int,
    reqSlotId: Int,
    slotNum: Int,
    created: Long)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      insert.into(RemodelSlot).columns(
        column.id,
        column.slotId,
        column.memberId,
        column.secondShip,
        column.fuel,
        column.ammo,
        column.steel,
        column.bauxite,
        column.develop,
        column.revamping,
        column.reqSlotId,
        column.slotNum,
        column.created
      ).values(
          id,
          slotId,
          memberId,
          secondShip,
          fuel,
          ammo,
          steel,
          bauxite,
          develop,
          revamping,
          reqSlotId,
          slotNum,
          created
        )
    }.update().apply()
  }

  def bulkInsert(x: data.RemodelSlotlist, memberId: Long)(implicit session: DBSession = autoSession): Unit = {
    val created = System.currentTimeMillis()
    val size = x.list.size
    val second = x.second.flatMap { ship => Ship.findShipId(memberId, ship) }
    applyUpdate {
      insert.into(RemodelSlot).columns(
        column.id,
        column.slotId,
        column.memberId,
        column.secondShip,
        column.fuel,
        column.ammo,
        column.steel,
        column.bauxite,
        column.develop,
        column.revamping,
        column.reqSlotId,
        column.slotNum,
        column.created
      ).multiValues(
          x.list.map(_.id),
          x.list.map(_.slotId),
          Seq.fill(size)(memberId),
          Seq.fill(size)(second),
          x.list.map(_.fuel),
          x.list.map(_.ammo),
          x.list.map(_.steel),
          x.list.map(_.bauxite),
          x.list.map(_.develop),
          x.list.map(_.revamping),
          x.list.map(_.reqSlotId),
          x.list.map(_.slotNum),
          Seq.fill(size)(created)
        )
    }
  }

  def save(entity: RemodelSlot)(implicit session: DBSession = autoSession): RemodelSlot = {
    withSQL {
      update(RemodelSlot).set(
        column.id -> entity.id,
        column.slotId -> entity.slotId,
        column.memberId -> entity.memberId,
        column.fuel -> entity.fuel,
        column.ammo -> entity.ammo,
        column.steel -> entity.steel,
        column.bauxite -> entity.bauxite,
        column.develop -> entity.develop,
        column.revamping -> entity.revamping,
        column.reqSlotId -> entity.reqSlotId,
        column.slotNum -> entity.slotNum,
        column.created -> entity.created
      ).where.eq(column.id, entity.id).and.eq(column.slotId, entity.slotId).and.eq(column.memberId, entity.memberId).and.eq(column.fuel, entity.fuel).and.eq(column.ammo, entity.ammo).and.eq(column.steel, entity.steel).and.eq(column.bauxite, entity.bauxite).and.eq(column.develop, entity.develop).and.eq(column.revamping, entity.revamping).and.eq(column.reqSlotId, entity.reqSlotId).and.eq(column.slotNum, entity.slotNum).and.eq(column.created, entity.created)
    }.update().apply()
    entity
  }

  def destroy(entity: RemodelSlot)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(RemodelSlot).where.eq(column.id, entity.id).and.eq(column.slotId, entity.slotId).and.eq(column.memberId, entity.memberId).and.eq(column.fuel, entity.fuel).and.eq(column.ammo, entity.ammo).and.eq(column.steel, entity.steel).and.eq(column.bauxite, entity.bauxite).and.eq(column.develop, entity.develop).and.eq(column.revamping, entity.revamping).and.eq(column.reqSlotId, entity.reqSlotId).and.eq(column.slotNum, entity.slotNum).and.eq(column.created, entity.created)
    }.update().apply()
  }

}
