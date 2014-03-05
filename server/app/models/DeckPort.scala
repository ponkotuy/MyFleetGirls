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

case class Mission(memberId: Long, deckId: Int, page: Int, number: Int, completeTime: Long, created: Long)

object Mission extends SQLSyntaxSupport[Mission] {
  def apply(x: SyntaxProvider[Mission])(rs: WrappedResultSet): Mission = apply(x.resultName)(rs)
  def apply(x: ResultName[Mission])(rs: WrappedResultSet): Mission = new Mission(
    rs.long(x.memberId),
    rs.int(x.deckId),
    rs.int(x.page),
    rs.int(x.number),
    rs.long(x.completeTime),
    rs.long(x.created)
  )

  lazy val m = Mission.syntax("m")

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

  def bulkInsert(ms: Seq[data.Mission], ids: Seq[Int], memberId: Long, created: Long = System.currentTimeMillis())(
      implicit session: DBSession = Mission.autoSession): Seq[Mission] = {
    applyUpdate {
      insert.into(Mission).columns(
        column.memberId, column.deckId,
        column.page, column.number, column.completeTime, column.created
      ).multiValues(
          Seq.fill(ms.size)(memberId), ids,
          ms.map(_.page), ms.map(_.number), ms.map(_.completeTime), Seq.fill(ms.size)(created)
        )
    }
    ms.zip(ids).map { case (f, i) => Mission(memberId, i, f.page, f.number, f.completeTime, created) }
  }

  def findByDeck(memberId: Long, deckId: Int)(
      implicit session: DBSession = Mission.autoSession): Option[Mission] = withSQL {
    select.from(Mission as m)
      .where.eq(m.memberId, memberId).and.eq(m.deckId, deckId)
      .limit(1)
  }.map(Mission(m)).toOption().apply()

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
  def checkUnique(memberId: Long, deckId: Int): Boolean =
    findByDeck(memberId, deckId).nonEmpty
}

/**
 *
 * @param deckId : 艦隊No.
 * @param num : 順番（旗艦0）
 */
case class DeckShip(deckId: Int, num: Int, memberId: Long, shipId: Int)

object DeckShip extends SQLSyntaxSupport[DeckShip] {
//  override val columns = Seq("id", "deck_id", "num", "member_id", "ship_id")
  def apply(x: SyntaxProvider[DeckShip])(rs: WrappedResultSet): DeckShip = apply(x.resultName)(rs)
  def apply(x: ResultName[DeckShip])(rs: WrappedResultSet): DeckShip = new DeckShip(
    rs.int(x.deckId),
    rs.int(x.num),
    rs.long(x.memberId),
    rs.int(x.shipId)
  )

  lazy val ds = DeckShip.syntax("ds")

  def create(deckId: Int, num: Int, memberId: Long, shipId: Int)(
      implicit session: DBSession = DeckShip.autoSession): DeckShip = {
    applyUpdate {
      insert.into(DeckShip).namedValues(
        column.deckId -> deckId, column.num -> num, column.memberId -> memberId, column.shipId -> shipId
      )
    }
    DeckShip(deckId, num, memberId, shipId)
  }

  def bulkInsert(deckId: Int, memberId: Long, ships: List[Int])(
      implicit session: DBSession = DeckShip.autoSession): List[DeckShip] = {
    require(ships.nonEmpty)
    deleteByDeck(deckId, memberId)
    applyUpdate {
      insert.into(DeckShip)
        .columns(column.deckId, column.num, column.memberId, column.shipId)
        .multiValues(Seq.fill(ships.size)(deckId), Seq.range(0, ships.size), Seq.fill(ships.size)(memberId), ships)
    }
    ships.zip(Stream.from(0)).map { case (ship, num) =>
      DeckShip(deckId, num, memberId, ship)
    }
  }

  def deleteByDeck(deckId: Int, memberId: Long)(
      implicit session: DBSession = DeckShip.autoSession): Unit = applyUpdate {
    delete.from(DeckShip)
      .where.eq(DeckShip.column.memberId, memberId).and.eq(DeckShip.column.deckId, deckId)
  }

  def deleteByUser(memberId: Long)(
      implicit session: DBSession = DeckShip.autoSession): Unit = applyUpdate {
    delete.from(DeckShip)
      .where.eq(DeckShip.column.memberId, memberId)
  }
}
