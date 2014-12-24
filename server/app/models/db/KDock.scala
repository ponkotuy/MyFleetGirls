package models.db

import models.join.KDockWithName
import scalikejdbc._
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
    select(kd.id, kd.memberId, kd.completeTime, kd.fuel, kd.ammo, kd.steel, kd.bauxite, ms.id, ms.name)
      .from(KDock as kd)
      .innerJoin(MasterShipBase as ms).on(kd.shipId, ms.id)
      .where.eq(kd.memberId, memberId)
      .orderBy(kd.id)
  }.map { rs =>
    KDockWithName(
      rs.int(kd.id), rs.long(kd.memberId), rs.long(kd.completeTime),
      rs.int(kd.fuel), rs.int(kd.ammo), rs.int(kd.steel), rs.int(kd.bauxite), rs.int(ms.id), rs.string(ms.name))
  }.toList().apply()

  def create(kd: data.KDock, memberId: Long)(implicit session: DBSession = KDock.autoSession): Unit = {
    val created = System.currentTimeMillis()
    applyUpdate {
      insert.into(KDock).namedValues(
        column.id -> kd.id, column.memberId -> memberId, column.shipId -> kd.shipId,
        column.state -> kd.state, column.completeTime -> kd.completeTime,
        column.fuel -> kd.fuel, column.ammo -> kd.ammo, column.steel -> kd.steel, column.bauxite -> kd.bauxite,
        column.created -> created
      )
    }
  }

  def bulkInsert(kds: Seq[data.KDock], memberId: Long)(implicit session: DBSession = KDock.autoSession): Unit = {
    if(kds.nonEmpty) {
      val created = System.currentTimeMillis()
      applyUpdate {
        insert.into(KDock).columns(
          column.id, column.memberId, column.shipId, column.state, column.completeTime,
          column.fuel, column.ammo, column.steel, column.bauxite, column.created
        ).multiValues(
            kds.map(_.id), Seq.fill(kds.size)(memberId), kds.map(_.shipId), kds.map(_.state), kds.map(_.completeTime),
            kds.map(_.fuel), kds.map(_.ammo), kds.map(_.steel), kds.map(_.bauxite), Seq.fill(kds.size)(created)
          )
      }
    }
  }

  def deleteByUser(memberId: Long)(implicit session: DBSession = KDock.autoSession): Unit = applyUpdate {
    delete.from(KDock).where.eq(KDock.column.memberId, memberId)
  }

  def destroy(memberId: Long, kDockId: Int)(implicit session: DBSession = autoSession): Unit = applyUpdate {
    delete.from(KDock).where.eq(column.memberId, memberId).and.eq(column.id, kDockId)
  }
}


