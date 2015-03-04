package models.db

import models.join.MissionWithFlagship
import models.other.MissionWithDeckId
import scalikejdbc._
import com.ponkotuy.data
import util.scalikejdbc.BulkInsert._

/**
 *
 * @author ponkotuy
 * Date:14/03/10.
 */
case class Mission(memberId: Long, deckId: Int, page: Int, number: Int, completeTime: Long, created: Long)

object Mission extends SQLSyntaxSupport[Mission] {

  def apply(x: SyntaxProvider[Mission])(rs: WrappedResultSet): Mission = apply(x.resultName)(rs)
  def apply(x: ResultName[Mission])(rs: WrappedResultSet): Mission = autoConstruct(rs, x)

  val m = Mission.syntax("m")
  val mm = MasterMission.syntax("mm")
  val dp = DeckPort.syntax("dp")
  val ds = DeckShip.syntax("ds")
  val s = Ship.syntax("s")

  def create(m: data.Mission, memberId: Long, deckId: Int, created: Long = System.currentTimeMillis())(
      implicit session: DBSession = Mission.autoSession): Mission = {
    applyUpdate {
      insert.into(Mission).namedValues(
        column.memberId -> memberId, column.deckId -> deckId,
        column.page -> m.page, column.number -> m.number, column.completeTime -> m.completeTime,
        column.created -> created
      )
    }
    Mission(memberId, deckId, m.page, m.number, m.completeTime, created)
  }

  def bulkInsert(ms: Seq[MissionWithDeckId], memberId: Long, created: Long = System.currentTimeMillis())(
      implicit session: DBSession = Mission.autoSession): Seq[Mission] = {
    if(ms.isEmpty) return Nil
    applyUpdate {
      insert.into(Mission).columns(
        column.memberId, column.deckId,
        column.page, column.number, column.completeTime, column.created
      ).multiValues(
          Seq.fill(ms.size)(memberId), ms.map(_.deckId),
          ms.map(_.page), ms.map(_.number), ms.map(_.completeTime), Seq.fill(ms.size)(created)
        )
    }
    ms.map { m => Mission(memberId, m.deckId, m.page, m.number, m.completeTime, created) }
  }

  def findByDeck(memberId: Long, deckId: Int)(
      implicit session: DBSession = Mission.autoSession): Option[Mission] = withSQL {
    select.from(Mission as m)
      .where.eq(m.memberId, memberId).and.eq(m.deckId, deckId)
      .limit(1)
  }.map(Mission(m)).toOption().apply()

  def findByUserWithName(memberId: Long)(implicit session: DBSession = Mission.autoSession): List[MissionWithName] =
    withSQL {
      select(m.number, mm.name, m.deckId, dp.name, m.completeTime).from(Mission as m)
        .leftJoin(MasterMission as mm).on(m.number, mm.id)
        .leftJoin(DeckPort as dp).on(sqls"${m.memberId} = ${dp.memberId} and ${m.deckId} = ${dp.id}")
        .where.eq(m.memberId, memberId)
    }.map(MissionWithName(m, mm, dp)).list().apply()

  def findByUserWithFlagship(memberId: Long)(implicit session: DBSession = autoSession): List[MissionWithFlagship] =
    withSQL {
      select.from(Mission as m)
        .innerJoin(MasterMission as mm).on(m.number, mm.id)
        .innerJoin(DeckPort as dp).on(sqls"${m.memberId} = ${dp.memberId} and ${m.deckId} = ${dp.id}")
        .innerJoin(DeckShip as ds).on(sqls"${m.memberId} = ${ds.memberId} and ${m.deckId} = ${ds.deckId}")
        .innerJoin(Ship as s).on(sqls"${m.memberId} = ${s.memberId} and ${ds.shipId} = ${s.id}")
        .where.eq(m.memberId, memberId).and.eq(ds.num, 0)
    }.map(MissionWithFlagship(m, mm, dp, s)).list().apply()

  def deleteByUser(memberId: Long)(
      implicit session: DBSession = Mission.autoSession): Unit = applyUpdate {
    delete.from(Mission).where.eq(Mission.column.memberId, memberId)
  }

  def deleteByDeck(memberId: Long, deckId: Int)(
      implicit session: DBSession = Mission.autoSession): Unit = applyUpdate {
    delete.from(Mission)
      .where.eq(Mission.column.memberId, memberId).and.eq(Mission.column.deckId, deckId)
  }

  // TODO もっと効率よく
  def checkUnique(memberId: Long, deckId: Int)(implicit session: DBSession = Mission.autoSession): Boolean =
    findByDeck(memberId, deckId).nonEmpty
}

case class MissionWithName(missionId: Int, missionName: String, deckId: Int, deckName: String, completeTime: Long)

object MissionWithName {
  def apply(m: SyntaxProvider[Mission], mm: SyntaxProvider[MasterMission], dp: SyntaxProvider[DeckPort])(
      rs: WrappedResultSet): MissionWithName =
    new MissionWithName(
      rs.int(m.number),
      rs.string(mm.name),
      rs.int(m.deckId),
      rs.string(dp.name),
      rs.long(m.completeTime)
    )
}
