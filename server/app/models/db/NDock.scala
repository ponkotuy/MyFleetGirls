package models.db

import scalikejdbc._
import com.ponkotuy.data

/**
 *
 * @author ponkotuy
 * Date: 14/03/02.
 */
case class NDock(id: Int, memberId: Long, shipId: Int, completeTime: Long, created: Long)

object NDock extends SQLSyntaxSupport[NDock] {
  def apply(x: SyntaxProvider[NDock])(rs: WrappedResultSet): NDock = apply(x.resultName)(rs)
  def apply(x: ResultName[NDock])(rs: WrappedResultSet): NDock = new NDock(
    rs.int(x.id),
    rs.long(x.memberId),
    rs.int(x.shipId),
    rs.long(x.completeTime),
    rs.long(x.created)
  )

  lazy val nd = NDock.syntax("nd")
  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")

  def findAllByUser(memberId: Long)(implicit session: DBSession = NDock.autoSession): List[NDock] = withSQL {
    select.from(NDock as nd)
      .where.eq(nd.memberId, memberId)
  }.map(NDock(nd)).toList().apply()

  def findAllByUserWithName(memberId: Long)(implicit session: DBSession = NDock.autoSession): List[NDockWithName] = {
    val result = withSQL {
      select(nd.id, nd.shipId, nd.completeTime, ms.id, ms.name).from(NDock as nd)
        .innerJoin(Ship as s).on(sqls"${nd.memberId} = ${s.memberId} and ${nd.shipId} = ${s.id}")
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .where.eq(nd.memberId, memberId)
        .orderBy(nd.id)
    }.map { rs =>
      NDockWithName(rs.int(nd.id), rs.int(nd.shipId), rs.long(nd.completeTime), rs.int(ms.id), rs.string(ms.name))
    }.toList().apply()
    result
  }

  def create(nd: data.NDock, memberId: Long)(implicit session: DBSession = NDock.autoSession): Unit = {
    val created = System.currentTimeMillis()
    applyUpdate {
      insert.into(NDock).namedValues(
        column.id -> nd.id,
        column.memberId -> memberId,
        column.shipId -> nd.shipId,
        column.completeTime -> nd.completeTime,
        column.created -> created
      )
    }
  }

  def deleteAllByUser(memberId: Long)(implicit session: DBSession = NDock.autoSession): Unit =
    applyUpdate { delete.from(NDock).where.eq(NDock.column.memberId, memberId) }
}

case class NDockWithName(id: Int, shipId: Int, completeTime: Long, masterShipId: Int, name: String)
