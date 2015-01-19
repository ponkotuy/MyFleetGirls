package models.db

import models.join.MissionHistoryShipWithMaster
import scalikejdbc._
import util.scalikejdbc.BulkInsert._

case class MissionHistoryShip(
  id: Int,
  shipId: Int,
  memberId: Long,
  missionId: Long,
  lv: Short,
  exp: Int,
  nowhp: Short,
  slot: String,
  fuel: Int,
  bull: Int,
  created: Long) {

  def save()(implicit session: DBSession = MissionHistoryShip.autoSession): MissionHistoryShip = MissionHistoryShip.save(this)(session)

  def destroy()(implicit session: DBSession = MissionHistoryShip.autoSession): Unit = MissionHistoryShip.destroy(this)(session)

}


object MissionHistoryShip extends SQLSyntaxSupport[MissionHistoryShip] {

  override val tableName = "mission_history_ship"

  override val columns = Seq("id", "ship_id", "member_id", "mission_id", "lv", "exp", "nowhp", "slot", "fuel", "bull", "created")

  def apply(mhs: SyntaxProvider[MissionHistoryShip])(rs: WrappedResultSet): MissionHistoryShip = apply(mhs.resultName)(rs)
  def apply(mhs: ResultName[MissionHistoryShip])(rs: WrappedResultSet): MissionHistoryShip = autoConstruct(rs, mhs)

  val mhs = MissionHistoryShip.syntax("mhs")
  val ms = MasterShipBase.syntax("ms")
  val mst = MasterStype.syntax("mst")

  override val autoSession = AutoSession

  def find(id: Int, missionId: Long)(implicit session: DBSession = autoSession): Option[MissionHistoryShip] = {
    withSQL {
      select.from(MissionHistoryShip as mhs).where.eq(mhs.id, id).and.eq(mhs.missionId, missionId)
    }.map(MissionHistoryShip(mhs.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MissionHistoryShip] = {
    withSQL(select.from(MissionHistoryShip as mhs)).map(MissionHistoryShip(mhs.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MissionHistoryShip as mhs)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MissionHistoryShip] = {
    withSQL {
      select.from(MissionHistoryShip as mhs).where.append(sqls"${where}")
    }.map(MissionHistoryShip(mhs.resultName)).list().apply()
  }

  def findAllWithMasterShipBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MissionHistoryShipWithMaster] = {
    withSQL {
      select.from(MissionHistoryShip as mhs)
        .innerJoin(MasterShipBase as ms).on(mhs.shipId, ms.id)
        .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where(where)
        .orderBy(mhs.missionId, mhs.id)
    }.map { rs =>
      val ship = MissionHistoryShip(mhs)(rs)
      val master = MasterShipBase(ms)(rs)
      val mstype = MasterStype(mst)(rs)
      MissionHistoryShipWithMaster(ship, master, mstype)
    }.list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MissionHistoryShip as mhs).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    id: Int,
    shipId: Int,
    memberId: Long,
    missionId: Long,
    lv: Short,
    exp: Int,
    nowhp: Short,
    slot: String,
    fuel: Int,
    bull: Int,
    created: Long)(implicit session: DBSession = autoSession): MissionHistoryShip = {
    withSQL {
      insert.into(MissionHistoryShip).columns(
        column.id,
        column.shipId,
        column.memberId,
        column.missionId,
        column.lv,
        column.exp,
        column.nowhp,
        column.slot,
        column.fuel,
        column.bull,
        column.created
      ).values(
          id,
          shipId,
          memberId,
          missionId,
          lv,
          exp,
          nowhp,
          slot,
          fuel,
          bull,
          created
        )
    }.update().apply()

    MissionHistoryShip(
      id = id,
      shipId = shipId,
      memberId = memberId,
      missionId = missionId,
      lv = lv,
      exp = exp,
      nowhp = nowhp,
      slot = slot,
      fuel = fuel,
      bull = bull,
      created = created)
  }

  def save(entity: MissionHistoryShip)(implicit session: DBSession = autoSession): MissionHistoryShip = {
    withSQL {
      update(MissionHistoryShip).set(
        column.id -> entity.id,
        column.shipId -> entity.shipId,
        column.memberId -> entity.memberId,
        column.missionId -> entity.missionId,
        column.lv -> entity.lv,
        column.exp -> entity.exp,
        column.nowhp -> entity.nowhp,
        column.slot -> entity.slot,
        column.fuel -> entity.fuel,
        column.bull -> entity.bull,
        column.created -> entity.created
      ).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId)
    }.update().apply()
    entity
  }

  def bulkInsert(ms: Seq[MissionHistoryShip])(implicit session: DBSession = autoSession): Unit = {
    if(ms.isEmpty) return
    applyUpdate {
      insert.into(MissionHistoryShip).columns(
        column.id,
        column.shipId,
        column.memberId,
        column.missionId,
        column.lv,
        column.exp,
        column.nowhp,
        column.slot,
        column.fuel,
        column.bull,
        column.created
      ).multiValues(
          ms.map(_.id),
          ms.map(_.shipId),
          ms.map(_.memberId),
          ms.map(_.missionId),
          ms.map(_.lv),
          ms.map(_.exp),
          ms.map(_.nowhp),
          ms.map(_.slot),
          ms.map(_.fuel),
          ms.map(_.bull),
          ms.map(_.created)
        )
    }
  }

  def destroy(entity: MissionHistoryShip)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MissionHistoryShip).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId)
    }.update().apply()
  }

}
