package models

import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.{WrappedResultSet, DBSession}

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

  def findAllByUser(memberId: Long)(implicit session: DBSession = NDock.autoSession): List[NDock] = withSQL {
    select.from(NDock as nd)
      .where.eq(nd.memberId, memberId)
  }.map(NDock(nd)).toList().apply()

  def create(nd: data.NDock)(implicit session: DBSession = NDock.autoSession): NDock = {
    val created = System.currentTimeMillis()
    applyUpdate {
      insert.into(NDock).namedValues(
        column.id -> nd.id,
        column.memberId -> nd.memberId,
        column.shipId -> nd.shipId,
        column.completeTime -> nd.completeTime,
        column.created -> created
      )
    }
    NDock(nd.id, nd.memberId, nd.shipId, nd.completeTime, created)
  }

  def deleteAllByUser(memberId: Long)(implicit session: DBSession = NDock.autoSession): Unit =
    applyUpdate { delete.from(NDock).where.eq(NDock.column.memberId, memberId) }
}
