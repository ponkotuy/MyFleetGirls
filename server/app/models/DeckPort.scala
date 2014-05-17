package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{DBSession, WrappedResultSet}
import com.ponkotuy.data
import util.scalikejdbc.BulkInsert._

/**
 *
 * @author ponkotuy
 * Date: 2014/03/04.
 */
case class DeckPort(id: Int, memberId: Long, name: String, created: Long)

object DeckPort extends SQLSyntaxSupport[DeckPort] {
  def apply(x: SyntaxProvider[DeckPort])(rs: WrappedResultSet): DeckPort = apply(x.resultName)(rs)
  def apply(x: ResultName[DeckPort])(rs: WrappedResultSet): DeckPort = new DeckPort(
    rs.int(x.id),
    rs.long(x.memberId),
    rs.string(x.name),
    rs.long(x.created)
  )

  lazy val dp = DeckPort.syntax("dp")

  def find(memberId: Long, deckId: Int)(implicit session: DBSession = autoSession): Option[DeckPort] = {
    withSQL {
      select.from(DeckPort as dp)
        .where.eq(dp.memberId, memberId).and.eq(dp.id, deckId)
    }.map(DeckPort(dp)).single().apply()
  }

  def findAllByUser(memberId: Long)(implicit session: DBSession = autoSession): List[DeckPort] = withSQL {
    select.from(DeckPort as dp).where.eq(dp.memberId, memberId)
  }.map(DeckPort(dp)).list().apply()

  def create(dp: data.DeckPort)(
      implicit session: DBSession = DeckPort.autoSession): Unit = {
    val created = System.currentTimeMillis()
    dp.mission match {
      case Some(mission) => Mission.create(mission, dp.memberId, dp.id, created)
      case None => Mission.deleteByDeck(dp.memberId, dp.id)
    }
    DeckShip.bulkInsert(dp.id, dp.memberId, dp.ships)
    applyUpdate {
      insert.into(DeckPort).namedValues(
        column.id -> dp.id, column.memberId -> dp.memberId,
        column.name -> dp.name, column.created -> created
      )
    }
  }

  def bulkInsert(dps: Seq[data.DeckPort])(
      implicit session: DBSession = DeckPort.autoSession): Seq[DeckPort] = {
    if(dps.isEmpty) return Nil
    require(dps.map(_.memberId).toSet.size == 1)
    val created = System.currentTimeMillis()
    val memberId = dps.head.memberId

    // Mission
    val (missions, ids) = dps.flatMap(d => d.mission.map(_ -> d.id)).unzip
    Mission.bulkInsert(missions, ids, memberId, created)

    // DeckShip
    dps.foreach { d => DeckShip.bulkInsert(d.id, d.memberId, d.ships) }

    // Main
    applyUpdate {
      insert.into(DeckPort)
        .columns(column.id, column.memberId, column.name, column.created)
        .multiValues(dps.map(_.id), dps.map(_.memberId), dps.map(_.name), Seq.fill(dps.size)(created))
    }
    dps.map { d => DeckPort(d.id, d.memberId, d.name, created) }
  }

  /** まとめて従属するTableまでdelete */
  def deleteByUser(memberId: Long)(
      implicit session: DBSession = DeckPort.autoSession): Unit = {
    Mission.deleteByUser(memberId)
    DeckShip.deleteByUser(memberId)
    applyUpdate { delete.from(DeckPort).where.eq(DeckPort.column.memberId, memberId) }
  }
}
