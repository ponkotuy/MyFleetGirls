package models

import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.{WrappedResultSet, DBSession}
import util.scalikejdbc.BulkInsert._

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
  lazy val ms = MasterShipBase.syntax("ms")

  def findAllByUser(memberId: Long)(implicit session: DBSession = KDock.autoSession): List[KDock] = withSQL {
    select.from(KDock as kd)
      .where.eq(kd.memberId, memberId)
      .orderBy(kd.id)
  }.map(KDock(kd)).toList().apply()

  def findAllByUserWithName(memberId: Long)(
      implicit session: DBSession = KDock.autoSession): List[KDockWithName] = withSQL {
    select(kd.id, kd.completeTime, kd.fuel, kd.ammo, kd.steel, kd.bauxite, ms.id, ms.name)
      .from(KDock as kd)
      .innerJoin(MasterShipBase as ms).on(kd.shipId, ms.id)
      .where.eq(kd.memberId, memberId)
      .orderBy(kd.id)
  }.map { rs =>
    KDockWithName(
      rs.int(kd.id), rs.long(kd.completeTime),
      rs.int(kd.fuel), rs.int(kd.ammo), rs.int(kd.steel), rs.int(kd.bauxite), rs.int(ms.id), rs.string(ms.name))
  }.toList().apply()

  def create(kd: data.KDock)(implicit session: DBSession = KDock.autoSession): KDock = {
    val created = System.currentTimeMillis()
    applyUpdate {
      insert.into(KDock).namedValues(
        column.id -> kd.id, column.memberId -> kd.memberId, column.shipId -> kd.shipId,
        column.state -> kd.state, column.completeTime -> kd.completeTime,
        column.fuel -> kd.fuel, column.ammo -> kd.ammo, column.steel -> kd.steel, column.bauxite -> kd.bauxite,
        column.created -> created
      )
    }
    KDock(kd.id, kd.memberId, kd.shipId, kd.state, kd.completeTime, kd.fuel, kd.ammo, kd.steel, kd.bauxite, created)
  }

  def bulkInsert(kds: Seq[data.KDock])(implicit session: DBSession = KDock.autoSession): Seq[KDock] = {
    if(kds.isEmpty) return Nil
    val created = System.currentTimeMillis()
    applyUpdate {
      insert.into(KDock).columns(
        column.id, column.memberId, column.shipId, column.state, column.completeTime,
        column.fuel, column.ammo, column.steel, column.bauxite, column.created
      ).multiValues(
          kds.map(_.id), kds.map(_.memberId), kds.map(_.shipId), kds.map(_.state), kds.map(_.completeTime),
          kds.map(_.fuel), kds.map(_.ammo), kds.map(_.steel), kds.map(_.bauxite), Seq.fill(kds.size)(created)
        )
    }
    kds.map { k =>
      KDock(k.id, k.memberId, k.shipId, k.state, k.completeTime, k.fuel, k.ammo, k.steel, k.bauxite, created)
    }
  }

  def deleteByUser(memberId: Long)(implicit session: DBSession = KDock.autoSession): Unit = applyUpdate {
    delete.from(KDock).where.eq(KDock.column.memberId, memberId)
  }
}

case class KDockWithName(
    id: Int, completeTime: Long,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int, shipId: Int, name: String)
