package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{DBSession, WrappedResultSet}
import util.scalikejdbc.BulkInsert._
import models.ShipWithName.RGB

/**
 *
 * @param deckId : 艦隊No.
 * @param num : 順番（旗艦0）
 */
case class DeckShip(deckId: Int, num: Int, memberId: Long, shipId: Int)

object DeckShip extends SQLSyntaxSupport[DeckShip] {
  def apply(x: SyntaxProvider[DeckShip])(rs: WrappedResultSet): DeckShip = apply(x.resultName)(rs)
  def apply(x: ResultName[DeckShip])(rs: WrappedResultSet): DeckShip = new DeckShip(
    rs.int(x.deckId),
    rs.int(x.num),
    rs.long(x.memberId),
    rs.int(x.shipId)
  )

  lazy val ds = DeckShip.syntax("ds")
  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")

  def findAllByUserWithName(memberId: Long)(implicit session: DBSession = autoSession): List[DeckShipWithName] = {
    withSQL {
      select(ds.deckId, ds.num, ds.memberId, ds.shipId, s.lv, s.cond, ms.name)
        .from(DeckShip as ds)
        .innerJoin(Ship as s).on(sqls"${ds.shipId} = ${s.id} and ${ds.memberId} = ${s.memberId}")
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .where.eq(ds.memberId, memberId)
    }.map(DeckShipWithName(ds, s, ms)).list().apply()
  }

  def findFlagshipByUserWishShipName(memberId: Long)(implicit session: DBSession = autoSession): Option[ShipWithName] = {
    withSQL {
      select.from(DeckShip as ds)
        .innerJoin(Ship as s).on(ds.shipId, s.id)
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .where.eq(ds.memberId, memberId).and.eq(s.memberId, memberId).and.eq(ds.deckId, 1).and.eq(ds.num, 0)
    }.map { rs => ShipWithName(Ship(s)(rs), MasterShipBase(ms)(rs)) }
      .first().apply()
  }

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

case class DeckShipWithName(deckId: Int, num: Int, memberId: Long, shipId: Int, lv: Int, cond: Int, name: String) {
  def rgb: RGB = ShipWithName.rgb(cond)
}

object DeckShipWithName {
  def apply(ds: SyntaxProvider[DeckShip], s: SyntaxProvider[Ship], ms: SyntaxProvider[MasterShipBase])(
      rs: WrappedResultSet): DeckShipWithName =
    new DeckShipWithName(
      rs.int(ds.deckId),
      rs.int(ds.num),
      rs.long(ds.memberId),
      rs.int(ds.shipId),
      rs.int(s.lv),
      rs.int(s.cond),
      rs.string(ms.name)
    )
}
