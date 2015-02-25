package models.db

import models.join.{ShipWithAdmiral, ShipWithName}
import scalikejdbc._

case class YomeShip(
  memberId: Long,
  sortNo: Short,
  shipId: Int) {

  def save()(implicit session: DBSession = YomeShip.autoSession): YomeShip = YomeShip.save(this)(session)

  def destroy()(implicit session: DBSession = YomeShip.autoSession): Unit = YomeShip.destroy(this)(session)

}


object YomeShip extends SQLSyntaxSupport[YomeShip] {

  val MaxYomeCount: Short = 4

  override val tableName = "yome_ship"

  override val columns = Seq("member_id", "sort_no", "ship_id")

  def apply(ys: SyntaxProvider[YomeShip])(rs: WrappedResultSet): YomeShip = autoConstruct(rs, ys)
  def apply(ys: ResultName[YomeShip])(rs: WrappedResultSet): YomeShip = autoConstruct(rs, ys)

  val ys = YomeShip.syntax("ys")
  val s = Ship.syntax("s")
  val ms = MasterShipBase.syntax("ms")
  val mst = MasterStype.syntax("mst")
  val mss = MasterShipSpecs.syntax("mss")
  val a = Admiral.syntax("a")

  override val autoSession = AutoSession

  def find(memberId: Long, sortNo: Short)(implicit session: DBSession = autoSession): Option[YomeShip] = {
    withSQL {
      select.from(YomeShip as ys).where.eq(ys.memberId, memberId).and.eq(ys.sortNo, sortNo)
    }.map(YomeShip(ys.resultName)).single().apply()
  }

  def findShipId(memberId: Long, shipId: Int)(implicit session: DBSession = autoSession): Option[YomeShip] = {
    withSQL {
      select.from(YomeShip as ys).where.eq(ys.memberId, memberId).and.eq(ys.shipId, shipId)
    }.map(YomeShip(ys)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[YomeShip] = {
    withSQL(select.from(YomeShip as ys)).map(YomeShip(ys.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(YomeShip as ys)).map(rs => rs.long(1)).single().apply().get
  }

  def countAllByShip()(implicit session: DBSession = autoSession): List[(Int, Long)] = {
    withSQL {
      select(ys.shipId, sqls.count).from(YomeShip as ys)
        .groupBy(ys.shipId)
    }.map { rs =>
      rs.int(1) -> rs.long(2)
    }.list().apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[YomeShip] = {
    withSQL {
      select.from(YomeShip as ys).where.append(sqls"${where}")
    }.map(YomeShip(ys.resultName)).list().apply()
  }

  def findAllFromMemberId(memberId: Long)(implicit session: DBSession = autoSession): List[YomeShip] =
    findAllBy(sqls"ys.member_id = ${memberId}")

  def findAllByWithName(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ShipWithName] = {
    withSQL {
      select.from(YomeShip as ys)
        .innerJoin(Ship as s).on(sqls"ys.member_id = s.member_id and ys.ship_id = s.id")
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
        .innerJoin(MasterShipSpecs as mss).on(s.shipId, mss.id)
        .where(where)
    }.map { rs =>
      val slot = Ship.findSlot(rs.long(ys.resultName.memberId), rs.int(ys.resultName.shipId))
      ShipWithName(Ship(s, slot)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs), MasterShipSpecs(mss)(rs))
    }.list().apply()
  }

  def findAllByWithAdmiral(where: SQLSyntax, limit: Int = 10, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[ShipWithAdmiral] = {
    withSQL {
      select.from(YomeShip as ys)
        .innerJoin(Ship as s).on(sqls"ys.member_id = s.member_id and ys.ship_id = s.id")
        .innerJoin(Admiral as a).on(ys.memberId, a.id)
        .where(where).orderBy(s.exp).desc
        .limit(limit).offset(offset)
    }.map { rs =>
      val slot = Ship.findSlot(rs.long(s.resultName.memberId), rs.int(s.resultName.id))
      ShipWithAdmiral(Ship(s, slot)(rs), Admiral(a)(rs))
    }.list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(YomeShip as ys).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    memberId: Long,
    sortNo: Short,
    shipId: Int)(implicit session: DBSession = autoSession): YomeShip = {
    withSQL {
      insert.into(YomeShip).columns(
        column.memberId,
        column.sortNo,
        column.shipId
      ).values(
          memberId,
          sortNo,
          shipId
        )
    }.update().apply()

    YomeShip(
      memberId = memberId,
      sortNo = sortNo,
      shipId = shipId)
  }

  def save(entity: YomeShip)(implicit session: DBSession = autoSession): YomeShip = {
    withSQL {
      update(YomeShip).set(
        column.memberId -> entity.memberId,
        column.sortNo -> entity.sortNo,
        column.shipId -> entity.shipId
      ).where.eq(column.memberId, entity.memberId).and.eq(column.sortNo, entity.sortNo)
    }.update().apply()
    entity
  }

  def destroy(entity: YomeShip)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(YomeShip).where.eq(column.memberId, entity.memberId).and.eq(column.sortNo, entity.sortNo)
    }.update().apply()
  }

}
