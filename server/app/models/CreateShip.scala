package models

import scalikejdbc._
import com.ponkotuy.data
import scalikejdbc.{WrappedResultSet, DBSession}
import sqls.distinct
import dat.{CreateShipWithName2, CreateShipWithName}

/** 建造ログ
  *
  * data.KDockのデータを用いて補完する必要がある
  *
  * @author ponkotuy
  * Date: 14/03/04.
  */
case class CreateShip(
    memberId: Long, resultShip: Int,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int, develop: Int,
    kDock: Int, highspeed: Boolean, largeFlag: Boolean, completeTime: Long, created: Long)

object CreateShip extends SQLSyntaxSupport[CreateShip] {
  def apply(x: SyntaxProvider[CreateShip])(rs: WrappedResultSet): CreateShip = apply(x.resultName)(rs)
  def apply(x: ResultName[CreateShip])(rs: WrappedResultSet): CreateShip = new CreateShip(
    rs.long(x.memberId),
    rs.int(x.resultShip),
    rs.int(x.fuel),
    rs.int(x.ammo),
    rs.int(x.steel),
    rs.int(x.bauxite),
    rs.int(x.develop),
    rs.int(x.kDock),
    rs.boolean(x.highspeed),
    rs.boolean(x.largeFlag),
    rs.long(x.completeTime),
    rs.long(x.created)
  )

  lazy val cs = CreateShip.syntax("cs")
  lazy val ms = MasterShipBase.syntax("ms")
  lazy val msb = MasterShipBase.syntax("msb") // MasterShipBaseを2種類使い分ける必要がある時用

  def findAllByUser(memberId: Long)(implicit session: DBSession = CreateShip.autoSession): List[CreateShip] = withSQL {
    select.from(CreateShip as cs)
      .where.eq(cs.memberId, memberId)
      .orderBy(cs.kDock)
  }.map(CreateShip(cs)).toList().apply()

  def findAllByUserWithName(memberId: Long, large: Boolean, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = CreateShip.autoSession): List[CreateShipWithName] = withSQL {
    select(cs.fuel, cs.ammo, cs.steel, cs.bauxite, cs.develop, cs.largeFlag, cs.created, ms.name)
      .from(CreateShip as cs)
      .innerJoin(MasterShipBase as ms).on(cs.resultShip, ms.id)
      .where.eq(cs.memberId, memberId).and.eq(cs.largeFlag, large)
      .orderBy(cs.created).desc
      .limit(limit).offset(offset)
  }.map(CreateShipWithName(cs, ms)).toList().apply()

  def findAllByMatWithName(m: Mat, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[CreateShipWithName2] = withSQL {
    select(cs.memberId, cs.resultShip, cs.largeFlag, cs.created, ms.name)
      .from(CreateShip as cs)
      .innerJoin(MasterShipBase as ms).on(cs.resultShip, ms.id)
      .where.eq(cs.fuel, m.fuel).and.eq(cs.ammo, m.ammo).and.eq(cs.steel, m.steel)
      .and.eq(cs.bauxite, m.bauxite).and.eq(cs.develop, m.develop)
      .orderBy(cs.created).desc
      .limit(limit).offset(offset)
  }.map(CreateShipWithName2(cs, ms)).toList().apply()

  def findAllShipByNameLike(q: String)(implicit session: DBSession = autoSession): List[MasterShipBase] = {
    withSQL {
      select(distinct(ms.resultAll)).from(CreateShip as cs)
        .innerJoin(MasterShipBase as ms).on(cs.resultShip, ms.id)
        .where.like(ms.name, q)
    }.map(MasterShipBase(ms)).toList().apply()
  }

  def countByMatWithMaster(m: Mat)(implicit session: DBSession = autoSession): List[(MasterShipBase, Long)] = withSQL {
    select(sqls"count(*) as count", ms.resultAll).from(CreateShip as cs)
      .innerJoin(MasterShipBase as ms).on(cs.resultShip, ms.id)
      .where.eq(cs.fuel, m.fuel).and.eq(cs.ammo, m.ammo).and.eq(cs.steel, m.steel).and.eq(cs.bauxite, m.bauxite)
      .and.eq(cs.develop, m.develop)
      .groupBy(cs.resultShip)
      .orderBy(sqls"count").desc
  }.map { rs => MasterShipBase(ms)(rs) -> rs.long(1) }.toList().apply()

  def countByUser(memberId: Long, large: Boolean)(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls"count(*)").from(CreateShip as cs)
      .where.eq(cs.memberId, memberId).and.eq(cs.largeFlag, large)
  }.map(_.long(1)).single().apply().get

  def materialCount(where: SQLSyntax = sqls"true")(implicit session: DBSession = autoSession): List[(Mat, Long)] = withSQL {
    select(cs.fuel, cs.ammo, cs.steel, cs.bauxite, cs.develop, sqls"count(*) as count")
      .from(CreateShip as cs)
      .where(where)
      .groupBy(cs.fuel, cs.ammo, cs.steel, cs.bauxite, cs.develop)
      .orderBy(sqls"count").desc
  }.map(rs => (Mat(cs)(rs), rs.long(6))).toList().apply()

  def createFromKDock(cs: data.CreateShip, kd: data.KDock, memberId: Long)(
      implicit session: DBSession = CreateShip.autoSession): Unit = {
    require(cs.equalKDock(kd))
    val created = System.currentTimeMillis()
    applyUpdate {
      insert.into(CreateShip).namedValues(
        column.memberId -> memberId, column.resultShip -> kd.shipId,
        column.fuel -> cs.fuel, column.ammo -> cs.ammo, column.steel -> cs.steel, column.bauxite -> cs.bauxite,
        column.develop -> cs.develop, column.kDock -> cs.kDock, column.highspeed -> cs.highspeed,
        column.largeFlag -> cs.largeFlag, column.completeTime -> kd.completeTime, column.created -> created
      )
    }
  }

  def create(cs: data.CreateShip, memberId: Long, resultShip: Int)(implicit session: DBSession = autoSession): Unit = {
    val created = System.currentTimeMillis()
    applyUpdate {
      insert.into(CreateShip).namedValues(
        column.memberId -> memberId, column.resultShip -> resultShip,
        column.fuel -> cs.fuel, column.ammo -> cs.ammo, column.steel -> cs.steel, column.bauxite -> cs.bauxite,
        column.develop -> cs.develop, column.kDock -> cs.kDock, column.highspeed -> cs.highspeed,
        column.largeFlag -> cs.largeFlag, column.completeTime -> created, column.created -> created
      )
    }
  }
}

case class Mat(fuel: Int, ammo: Int, steel: Int, bauxite: Int, develop: Int)

object Mat {
  def apply(cs: SyntaxProvider[CreateShip])(rs: WrappedResultSet): Mat =
    new Mat(
      rs.int(cs.fuel),
      rs.int(cs.ammo),
      rs.int(cs.steel),
      rs.int(cs.bauxite),
      rs.int(cs.develop)
    )
}

case class MiniShip(id: Int, name: String)
