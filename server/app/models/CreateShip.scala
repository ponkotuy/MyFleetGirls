package models

import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.{WrappedResultSet, DBSession}

/** 建造ログ(data.CreateShipとはかなり異なる)
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
  lazy val ms = MasterShip.syntax("ms")

  def findAllByUser(memberId: Long)(implicit session: DBSession = CreateShip.autoSession): List[CreateShip] = withSQL {
    select.from(CreateShip as cs)
      .where.eq(cs.memberId, memberId)
      .orderBy(cs.kDock)
  }.map(CreateShip(cs)).toList().apply()

  def findAllByUserWithName(memberId: Long, large: Boolean, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = CreateShip.autoSession): List[CreateShipWithName] = withSQL {
    select(cs.fuel, cs.ammo, cs.steel, cs.bauxite, cs.develop, cs.largeFlag, cs.created, ms.name)
      .from(CreateShip as cs)
      .innerJoin(MasterShip as ms).on(cs.resultShip, ms.id)
      .where.eq(cs.memberId, memberId).and.eq(cs.largeFlag, large)
      .orderBy(cs.created).desc
      .limit(limit).offset(offset)
  }.map(CreateShipWithName(cs, ms)).toList().apply()

  def findAllByMatWithName(m: Mat, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[CreateShipWithName2] = withSQL {
    select(cs.memberId, cs.resultShip, cs.largeFlag, cs.created, ms.name)
      .from(CreateShip as cs)
      .innerJoin(MasterShip as ms).on(cs.resultShip, ms.id)
      .where.eq(cs.fuel, m.fuel).and.eq(cs.ammo, m.ammo).and.eq(cs.steel, m.steel)
      .and.eq(cs.bauxite, m.bauxite).and.eq(cs.develop, m.develop)
      .orderBy(cs.created).desc
      .limit(limit).offset(offset)
  }.map(CreateShipWithName2(cs, ms)).toList().apply()

  def countByMat(m: Mat)(implicit session: DBSession = autoSession): List[(MiniShip, Long)] = withSQL {
    select(cs.resultShip, ms.name, sqls"count(*) as count").from(CreateShip as cs)
      .innerJoin(MasterShip as ms).on(cs.resultShip, ms.id)
      .where.eq(cs.fuel, m.fuel).and.eq(cs.ammo, m.ammo).and.eq(cs.steel, m.steel).and.eq(cs.bauxite, m.bauxite)
      .and.eq(cs.develop, m.develop)
      .groupBy(cs.resultShip)
      .orderBy(sqls"count").desc
  }.map { rs => (MiniShip(rs.int(1), rs.string(2)), rs.long(3)) }.toList().apply()

  def countByUser(memberId: Long, large: Boolean)(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls"count(*)").from(CreateShip as cs)
      .where.eq(cs.memberId, memberId).and.eq(cs.largeFlag, large)
  }.map(_.long(1)).single().apply().get

  def materialCount()(implicit session: DBSession = autoSession): List[(Mat, Long)] = withSQL {
    select(cs.fuel, cs.ammo, cs.steel, cs.bauxite, cs.develop, sqls"count(*) as count")
      .from(CreateShip as cs)
      .groupBy(cs.fuel, cs.ammo, cs.steel, cs.bauxite, cs.develop)
      .orderBy(sqls"count")
  }.map(rs => (Mat(cs)(rs), rs.long(6))).toList().apply()

  def create(cs: data.CreateShip, kd: data.KDock)(
      implicit session: DBSession = CreateShip.autoSession): CreateShip = {
    require(cs.equalKDock(kd))
    val created = System.currentTimeMillis()
    applyUpdate {
      insert.into(CreateShip).namedValues(
        column.memberId -> kd.memberId, column.resultShip -> kd.shipId,
        column.fuel -> cs.fuel, column.ammo -> cs.ammo, column.steel -> cs.steel, column.bauxite -> cs.bauxite,
        column.develop -> cs.develop, column.kDock -> cs.kDock, column.highspeed -> cs.highspeed,
        column.largeFlag -> cs.largeFlag, column.completeTime -> kd.completeTime, column.created -> created
      )
    }
    CreateShip(
      kd.memberId, kd.shipId,
      cs.fuel, cs.ammo, cs.steel, cs.bauxite, cs.develop,
      cs.kDock, cs.highspeed, cs.largeFlag, kd.completeTime, created)
  }
}

case class CreateShipWithName(
    fuel: Int, ammo: Int, steel: Int, bauxite: Int, develop: Int,
    largeFlag: Boolean, created: Long, name: String)

object CreateShipWithName {
  def apply(cs: SyntaxProvider[CreateShip], ms: SyntaxProvider[MasterShip])(
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
  def apply(cs: SyntaxProvider[CreateShip], ms: SyntaxProvider[MasterShip])(
    rs: WrappedResultSet): CreateShipWithName2 =
    new CreateShipWithName2(
      rs.long(cs.memberId),
      rs.int(cs.resultShip),
      rs.boolean(cs.largeFlag),
      rs.long(cs.created),
      rs.string(ms.name)
    )
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
