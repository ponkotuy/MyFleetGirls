package models.db

import models.other.MissionWithDeckId
import scalikejdbc._
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
  def apply(x: ResultName[DeckPort])(rs: WrappedResultSet): DeckPort = autoConstruct(rs, x)

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

  def create(dp: data.DeckPort, memberId: Long)(
      implicit session: DBSession = DeckPort.autoSession): Unit = {
    val created = System.currentTimeMillis()
    dp.mission match {
      case Some(mission) => Mission.create(mission, memberId, dp.id, created)
      case None => Mission.deleteByDeck(memberId, dp.id)
    }
    DeckShip.bulkInsert(dp.id, memberId, dp.ships)
    applyUpdate {
      insert.into(DeckPort).namedValues(
        column.id -> dp.id, column.memberId -> memberId,
        column.name -> dp.name, column.created -> created
      )
    }
  }

  def bulkInsert(dps: Seq[data.DeckPort], memberId: Long)(
      implicit session: DBSession = DeckPort.autoSession): Unit = {
    if(dps.nonEmpty) {
      val created = System.currentTimeMillis()

      // Mission
      val missions = dps.flatMap(d => d.mission.map(MissionWithDeckId(_, d.id)))
      Mission.bulkInsert(missions, memberId, created)
      MissionHistory.bulkInsert(missions, memberId, created)

      // DeckShip
      dps.foreach { d => DeckShip.bulkInsert(d.id, memberId, d.ships)}

      // Main
      applyUpdate {
        insert.into(DeckPort)
          .columns(column.id, column.memberId, column.name, column.created)
          .multiValues(dps.map(_.id), Seq.fill(dps.size)(memberId), dps.map(_.name), Seq.fill(dps.size)(created))
      }
    }
  }

  /** まとめて従属するTableまでdelete */
  def deleteByUser(memberId: Long)(
      implicit session: DBSession = DeckPort.autoSession): Unit = {
    Mission.deleteByUser(memberId)
    DeckShip.deleteByUser(memberId)
    applyUpdate { delete.from(DeckPort).where.eq(DeckPort.column.memberId, memberId) }
  }
}
