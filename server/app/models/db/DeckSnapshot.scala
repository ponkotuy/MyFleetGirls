package models.db

import models.join.{ShipSnapshotWithItem, ShipSnapshotWithName, DeckSnapshotWithAdmiral, DeckSnapshotWithShip}
import scalikejdbc._

case class DeckSnapshot(
    id: Long,
    memberId: Long,
    name: String,
    title: String,
    comment: String,
    created: Long,
    sortOrder: Int) {

  def save()(implicit session: DBSession = DeckSnapshot.autoSession): DeckSnapshot = DeckSnapshot.save(this)(session)

  def destroy()(implicit session: DBSession = DeckSnapshot.autoSession): Unit = DeckSnapshot.destroy(this)(session)

}


object DeckSnapshot extends SQLSyntaxSupport[DeckSnapshot] {

  override val tableName = "deck_snapshot"

  override val columns = Seq("id", "member_id", "name", "title", "comment", "created", "sort_order")

  def apply(ds: SyntaxProvider[DeckSnapshot])(rs: WrappedResultSet): DeckSnapshot = apply(ds.resultName)(rs)
  def apply(ds: ResultName[DeckSnapshot])(rs: WrappedResultSet): DeckSnapshot = autoConstruct(rs, ds)

  val ds = DeckSnapshot.syntax("ds")
  val dss = DeckShipSnapshot.syntax("dss")
  val a = Admiral.syntax("a")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[DeckSnapshot] = {
    withSQL {
      select.from(DeckSnapshot as ds).where.eq(ds.id, id)
    }.map(DeckSnapshot(ds.resultName)).single().apply()
  }

  def findWithShip(id: Long)(implicit session: DBSession = autoSession): Option[DeckSnapshotWithShip] = {
    val deck = find(id)
    deck.map { d =>
      val ships = DeckShipSnapshot.findAllByWithName(sqls"deck_id = ${d.id}")
      DeckSnapshotWithShip(d, ships.map(withItems))
    }
  }

  def withItems(ship: ShipSnapshotWithName)(implicit session: DBSession = autoSession): ShipSnapshotWithItem = {
    val is = ItemSnapshot.is
    val items = ItemSnapshot.findAllBy(sqls.eq(is.shipSnapshotId, ship.rest.id)).sortBy(_.position)
    ship.withItem(items)
  }

  def findAll()(implicit session: DBSession = autoSession): List[DeckSnapshot] = {
    withSQL(select.from(DeckSnapshot as ds)).map(DeckSnapshot(ds.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(DeckSnapshot as ds)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[DeckSnapshot] = {
    withSQL {
      select.from(DeckSnapshot as ds).where.append(sqls"${where}")
        .orderBy(ds.created).desc
    }.map(DeckSnapshot(ds.resultName)).list().apply()
  }

  def findAllByWithShip(where: SQLSyntax)(implicit session: DBSession = autoSession): List[DeckSnapshotWithShip] = {
    val decks = findAllBy(where)
    if(decks.isEmpty) Nil
    else {
      val ids = decks.map(_.id)
      val ships = DeckShipSnapshot.findAllByWithName(sqls"deck_id in (${ids})")
      decks.map { deck =>
        val deckShip = ships.filter(_.deckId == deck.id).sortBy(_.num)
        DeckSnapshotWithShip(deck, deckShip.map(withItems))
      }
    }
  }

  def findAllByWithAdmiral(where: SQLSyntax, limit: Int = Int.MaxValue, offset: Int = 0)(implicit session: DBSession = autoSession): List[DeckSnapshotWithAdmiral] = {
    val deckWithAdmiral = withSQL {
      select.from(DeckSnapshot as ds)
        .innerJoin(Admiral as a).on(ds.memberId, a.id)
        .where(where)
        .orderBy(ds.created).desc
        .limit(limit).offset(offset)
    }.map { rs =>
      (Admiral(a)(rs), DeckSnapshot(ds)(rs))
    }.list().apply()
    if(deckWithAdmiral.nonEmpty) {
      val ids = deckWithAdmiral.map(_._2.id)
      val ships = DeckShipSnapshot.findAllByWithName(sqls"deck_id in (${ids})")
      deckWithAdmiral.map { case (admiral, deck) =>
        val deckShip = ships.filter(_.deckId == deck.id).sortBy(_.num)
        DeckSnapshotWithAdmiral(deck, deckShip.map(withItems), admiral)
      }
    } else {
      Nil
    }
  }

  def findAllOrder(memberId: Long)(implicit session: DBSession = autoSession): List[DeckSnapshot] = {
    val snaps = findAllBy(sqls.eq(ds.memberId, memberId))
    snaps.zipWithIndex.sortBy { case (snap, idx) => idx + snap.sortOrder }.map(_._1)
  }

  def findAllOrderWithShip(memberId: Long)(implicit session: DBSession = autoSession): List[DeckSnapshotWithShip] = {
    val snaps = findAllByWithShip(sqls.eq(ds.memberId, memberId))
    snaps.zipWithIndex.sortBy { case (snap, idx) => idx + snap.sortorder }.map(_._1)
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(DeckSnapshot as ds).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
      memberId: Long,
      name: String,
      title: String,
      comment: String,
      created: Long,
      sortOrder: Int = 0)(implicit session: DBSession = autoSession): DeckSnapshot = {
    val generatedKey = withSQL {
      insert.into(DeckSnapshot).columns(
        column.memberId,
        column.name,
        column.title,
        column.comment,
        column.created,
        column.sortOrder
      ).values(
          memberId,
          name,
          title,
          comment,
          created,
          sortOrder
      )
    }.updateAndReturnGeneratedKey().apply()

    DeckSnapshot(
      id = generatedKey,
      memberId = memberId,
      name = name,
      title = title,
      comment = comment,
      created = created,
      sortOrder = sortOrder)
  }

  def save(entity: DeckSnapshot)(implicit session: DBSession = autoSession): DeckSnapshot = {
    withSQL {
      update(DeckSnapshot).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.name -> entity.name,
        column.title -> entity.title,
        column.comment -> entity.comment,
        column.created -> entity.created,
        column.sortOrder -> entity.sortOrder
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: DeckSnapshot)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(DeckSnapshot).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
