package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data

case class ShipBook(
  memberId: Long,
  id: Int,
  indexNo: Int,
  isDameged: Boolean,
  name: String,
  updated: Long) {

  def save()(implicit session: DBSession = ShipBook.autoSession): ShipBook = ShipBook.save(this)(session)

  def destroy()(implicit session: DBSession = ShipBook.autoSession): Unit = ShipBook.destroy(this)(session)

}


object ShipBook extends SQLSyntaxSupport[ShipBook] {

  override val tableName = "ship_book"

  override val columns = Seq("member_id", "id", "index_no", "is_dameged", "name", "updated")

  def apply(sb: ResultName[ShipBook])(rs: WrappedResultSet): ShipBook = new ShipBook(
    memberId = rs.long(sb.memberId),
    id = rs.int(sb.id),
    indexNo = rs.int(sb.indexNo),
    isDameged = rs.boolean(sb.isDameged),
    name = rs.string(sb.name),
    updated = rs.long(sb.updated)
  )

  val sb = ShipBook.syntax("sb")
  val ms = MasterShip.syntax("ms")

  override val autoSession = AutoSession

  def find(indexNo: Int, memberId: Long)(implicit session: DBSession = autoSession): Option[ShipBook] = {
    withSQL {
      select.from(ShipBook as sb).where.eq(sb.indexNo, indexNo).and.eq(sb.memberId, memberId)
    }.map(ShipBook(sb.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ShipBook] = {
    withSQL(select.from(ShipBook as sb)).map(ShipBook(sb.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(ShipBook as sb)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ShipBook] = {
    withSQL {
      select.from(ShipBook as sb).where.append(sqls"${where}")
    }.map(ShipBook(sb.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(ShipBook as sb).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    memberId: Long,
    id: Int,
    indexNo: Int,
    isDameged: Boolean,
    name: String,
    updated: Long)(implicit session: DBSession = autoSession): ShipBook = {
    withSQL {
      insert.into(ShipBook).columns(
        column.memberId,
        column.id,
        column.indexNo,
        column.isDameged,
        column.name,
        column.updated
      ).values(
          memberId,
          id,
          indexNo,
          isDameged,
          name,
          updated
        )
    }.update().apply()

    ShipBook(
      memberId = memberId,
      id = id,
      indexNo = indexNo,
      isDameged = isDameged,
      name = name,
      updated = updated)
  }

  def bulkUpsert(xs: Seq[data.ShipBook])(implicit session: DBSession = autoSession): Seq[ShipBook] = {
    val now = System.currentTimeMillis()
    val params = xs.map { x =>
      Seq(x.memberId, x.id, x.indexNo, x.isDamaged, x.name, now)
    }
    sql"replace into ship_book (member_id, id, index_no, is_dameged, name, updated) values (?, ?, ?, ?, ?, ?)"
      .batch(params:_*).apply()
    xs.map { x => ShipBook(x.memberId, x.id, x.indexNo, x.isDamaged, x.name, now) }
  }

  def save(entity: ShipBook)(implicit session: DBSession = autoSession): ShipBook = {
    withSQL {
      update(ShipBook).set(
        column.memberId -> entity.memberId,
        column.id -> entity.id,
        column.indexNo -> entity.indexNo,
        column.isDameged -> entity.isDameged,
        column.name -> entity.name,
        column.updated -> entity.updated
      ).where.eq(column.indexNo, entity.indexNo).and.eq(column.memberId, entity.memberId)
    }.update().apply()
    entity
  }

  def destroy(entity: ShipBook)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(ShipBook).where.eq(column.indexNo, entity.indexNo).and.eq(column.memberId, entity.memberId)
    }.update().apply()
  }

}
