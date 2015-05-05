package models.db

import honor.{Honors, HonorCategory}
import models.join.HonorWithAdmiral
import scalikejdbc._

case class Honor(
    id: Long,
    memberId: Long,
    category: Int,
    name: String,
    setBadge: Boolean,
    invisible: Boolean) {

  def save()(implicit session: DBSession = Honor.autoSession): Honor = Honor.save(this)(session)

  def destroy()(implicit session: DBSession = Honor.autoSession): Unit = Honor.destroy(this)(session)

  def categoryOpt: Option[HonorCategory] = Honors.category(category)

  def comment: String = categoryOpt.map(_.comment).getOrElse("")
}


object Honor extends SQLSyntaxSupport[Honor] {

  override val tableName = "honor"

  override val columns = Seq("id", "member_id", "category", "name", "set_badge", "invisible")

  def apply(h: SyntaxProvider[Honor])(rs: WrappedResultSet): Honor = apply(h.resultName)(rs)
  def apply(h: ResultName[Honor])(rs: WrappedResultSet): Honor = autoConstruct(rs, h)

  val h = Honor.syntax("h")
  val a = Admiral.syntax("a")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Honor] = {
    withSQL {
      select.from(Honor as h).where.eq(h.id, id)
    }.map(Honor(h.resultName)).single().apply()
  }

  def findName(memberId: Long, name: String)(implicit session: DBSession = autoSession): Option[Honor] = withSQL {
    select.from(Honor as h).where.eq(h.memberId, memberId).and.eq(h.name, name)
  }.map(Honor(h)).single().apply()

  def findAll()(implicit session: DBSession = autoSession): List[Honor] = {
    withSQL(select.from(Honor as h)).map(Honor(h.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Honor as h)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Honor] = {
    withSQL {
      select.from(Honor as h).where.append(sqls"${where}")
    }.map(Honor(h.resultName)).list().apply()
  }

  def findAllByWithAdmiral(where: SQLSyntax)(implicit session: DBSession = autoSession): List[HonorWithAdmiral] = {
    val records = withSQL {
      select.from(Honor as h)
        .innerJoin(Admiral as a).on(h.memberId, a.id)
        .where(where)
    }.map { rs =>
      (Honor(h)(rs), Admiral(a)(rs))
    }.list().apply()
    HonorWithAdmiral.fromRecords(records)
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Honor as h).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def countName()(implicit session: DBSession = autoSession): Map[String, Long] = {
    withSQL {
      select(h.name, sqls.count).from(Honor as h).groupBy(h.name)
    }.map { rs =>
      rs.string(1) -> rs.long(2)
    }.list().apply().toMap
  }

  def create(
      memberId: Long,
      category: Int,
      name: String,
      setBadge: Boolean,
      invisible: Boolean)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      insert.into(Honor).columns(
        column.memberId,
        column.category,
        column.name,
        column.setBadge,
        column.invisible
      ).values(
            memberId,
            category,
            name,
            setBadge,
            invisible
          )
    }.updateAndReturnGeneratedKey().apply()
  }

  def save(entity: Honor)(implicit session: DBSession = autoSession): Honor = {
    withSQL {
      update(Honor).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.category -> entity.category,
        column.name -> entity.name,
        column.setBadge -> entity.setBadge,
        column.invisible -> entity.invisible
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def updateUnset(memberId: Long)(implicit session: DBSession = autoSession): Unit = applyUpdate {
    update(Honor).set(column.setBadge -> false).where.eq(column.memberId, memberId)
  }

  def destroy(entity: Honor)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(Honor).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
