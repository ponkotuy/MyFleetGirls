package models

import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.{WrappedResultSet, DBSession}

/**
 *
 * @author ponkotuy
 * Date: 14/03/04
 */
case class KDock(
    id: Int, memberId: Long, shipId: Int,
    state: Int, completeTime: Long,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int, created: Long)

object KDock extends SQLSyntaxSupport[KDock] {
  def apply(x: SyntaxProvider[KDock])(rs: WrappedResultSet): KDock = apply(x.resultName)(rs)
  def apply(x: ResultName[KDock])(rs: WrappedResultSet): KDock = new KDock(
    rs.int(x.id),
    rs.long(x.memberId),
    rs.int(x.shipId),
    rs.int(x.state),
    rs.long(x.completeTime),
    rs.int(x.fuel),
    rs.int(x.ammo),
    rs.int(x.steel),
    rs.int(x.bauxite),
    rs.long(x.created)
  )

  lazy val kd = KDock.syntax("kd")

  def findAllByUser(memberId: Long)(implicit session: DBSession = KDock.autoSession): List[KDock] = withSQL {
    select.from(KDock as kd)
      .where.eq(kd.memberId, memberId)
      .orderBy(kd.id)
  }.map(KDock(kd)).toList().apply()

  def create(kd: data.KDock)(implicit session: DBSession = KDock.autoSession): KDock = {
    val created = System.currentTimeMillis()
    withSQL {
      insert.into(KDock).namedValues(
        column.id -> kd.id, column.memberId -> kd.memberId, column.shipId -> kd.shipId,
        column.state -> kd.state, column.completeTime -> kd.completeTime,
        column.fuel -> kd.fuel, column.ammo -> kd.ammo, column.steel -> kd.steel, column.bauxite -> kd.bauxite,
        column.created -> created
      )
    }
    KDock(kd.id, kd.memberId, kd.shipId, kd.state, kd.completeTime, kd.fuel, kd.ammo, kd.steel, kd.bauxite, created)
  }
}
