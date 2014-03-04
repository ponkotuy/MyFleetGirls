package models

import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.{WrappedResultSet, DBSession}

/** 建造ログ(data.CreateShipとは微妙に異なる)
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

  def findAllByUser(memberId: Long)(implicit session: DBSession = CreateShip.autoSession): List[CreateShip] = withSQL {
    select.from(CreateShip as cs)
      .where.eq(cs.memberId, memberId)
      .orderBy(cs.kDock)
  }.map(CreateShip(cs)).toList().apply()

  def create(cs: data.CreateShip, kd: data.KDock)(
      implicit session: DBSession = CreateShip.autoSession): CreateShip = {
    require(cs.equalKDock(kd))
    val created = System.currentTimeMillis()
    withSQL {
      insert.into(CreateShip).namedValues(
        column.memberId -> kd.memberId, column.resultShip -> kd.shipId,
        column.fuel -> cs.fuel, column.ammo -> cs.ammo, column.steel -> cs.steel, column.bauxite -> cs.bauxite,
        column.develop -> cs.develop, column.kDock -> cs.kDock, column.highspeed -> cs.highspeed,
        column.largeFlag -> cs.largeFlag, column.completeTime -> kd.completeTime, column.created -> created
      )
    }.updateAndReturnGeneratedKey().apply()
    CreateShip(
      kd.memberId, kd.shipId,
      cs.fuel, cs.ammo, cs.steel, cs.bauxite, cs.develop,
      cs.kDock, cs.highspeed, cs.largeFlag, kd.completeTime, created)
  }
}
