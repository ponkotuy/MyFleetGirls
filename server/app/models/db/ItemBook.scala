package models.db

import com.ponkotuy.data
import scalikejdbc._

case class ItemBook(
  memberId: Long,
  id: Int,
  indexNo: Int,
  name: String,
  updated: Long) {

  def save()(implicit session: DBSession = ItemBook.autoSession): ItemBook = ItemBook.save(this)(session)

  def destroy()(implicit session: DBSession = ItemBook.autoSession): Unit = ItemBook.destroy(this)(session)

}


object ItemBook extends SQLSyntaxSupport[ItemBook] {

  override val tableName = "item_book"

  override val columns = Seq("member_id", "id", "index_no", "name", "updated")

  def apply(ib: ResultName[ItemBook])(rs: WrappedResultSet): ItemBook = new ItemBook(
    memberId = rs.long(ib.memberId),
    id = rs.int(ib.id),
    indexNo = rs.int(ib.indexNo),
    name = rs.string(ib.name),
    updated = rs.long(ib.updated)
  )

  val ib = ItemBook.syntax("ib")

  override val autoSession = AutoSession

  def find(indexNo: Int, memberId: Long)(implicit session: DBSession = autoSession): Option[ItemBook] = {
    withSQL {
      select.from(ItemBook as ib).where.eq(ib.indexNo, indexNo).and.eq(ib.memberId, memberId)
    }.map(ItemBook(ib.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ItemBook] = {
    withSQL(select.from(ItemBook as ib)).map(ItemBook(ib.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(ItemBook as ib)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ItemBook] = {
    withSQL {
      select.from(ItemBook as ib).where.append(sqls"${where}")
    }.map(ItemBook(ib.resultName)).list().apply()
  }

  def findAllUnique()(implicit session: DBSession = autoSession): List[(Int, String)] = withSQL {
    select(ib.indexNo, ib.name).from(ItemBook as ib).groupBy(ib.indexNo).orderBy(ib.indexNo)
  }.map { rs =>
    rs.int(1) -> rs.string(2)
  }.list().apply()

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(ItemBook as ib).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    memberId: Long,
    id: Int,
    indexNo: Int,
    name: String,
    updated: Long)(implicit session: DBSession = autoSession): ItemBook = {
    withSQL {
      insert.into(ItemBook).columns(
        column.memberId,
        column.id,
        column.indexNo,
        column.name,
        column.updated
      ).values(
          memberId,
          id,
          indexNo,
          name,
          updated
        )
    }.update().apply()

    ItemBook(
      memberId = memberId,
      id = id,
      indexNo = indexNo,
      name = name,
      updated = updated)
  }

  def bulkUpsert(xs: Seq[data.ItemBook], memberId: Long)(implicit session: DBSession = autoSession): Seq[ItemBook] = {
    val now = System.currentTimeMillis()
    val params = xs.map { x => Seq[Any](memberId, x.id, x.indexNo, x.name, now) }
    sql"""replace into item_book (member_id, id, index_no, name, updated)
          values (?, ?, ?, ?, ?)""".batch(params:_*).apply()
    xs.map { x => ItemBook(memberId, x.id, x.indexNo, x.name, now) }
  }

  def save(entity: ItemBook)(implicit session: DBSession = autoSession): ItemBook = {
    withSQL {
      update(ItemBook).set(
        column.memberId -> entity.memberId,
        column.id -> entity.id,
        column.indexNo -> entity.indexNo,
        column.name -> entity.name,
        column.updated -> entity.updated
      ).where.eq(column.indexNo, entity.indexNo).and.eq(column.memberId, entity.memberId)
    }.update().apply()
    entity
  }

  def destroy(entity: ItemBook)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(ItemBook).where.eq(column.indexNo, entity.indexNo).and.eq(column.memberId, entity.memberId)
    }.update().apply()
  }

}
