package models.db

import scalikejdbc._

case class Honor(
  id: Long,
  memberId: Long,
  category: Int,
  name: String,
  set: Boolean) {

  def save()(implicit session: DBSession = Honor.autoSession): Honor = Honor.save(this)(session)

  def destroy()(implicit session: DBSession = Honor.autoSession): Unit = Honor.destroy(this)(session)

}


object Honor extends SQLSyntaxSupport[Honor] {

  override val tableName = "honor"

  override val columns = Seq("id", "member_id", "category", "name", "set")

  def apply(h: SyntaxProvider[Honor])(rs: WrappedResultSet): Honor = apply(h.resultName)(rs)
  def apply(h: ResultName[Honor])(rs: WrappedResultSet): Honor = new Honor(
    id = rs.get(h.id),
    memberId = rs.get(h.memberId),
    category = rs.get(h.category),
    name = rs.get(h.name),
    set = rs.get(h.set)
  )

  val h = Honor.syntax("h")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Honor] = {
    withSQL {
      select.from(Honor as h).where.eq(h.id, id)
    }.map(Honor(h.resultName)).single().apply()
  }

  def find(memberId: Long, name: String)(implicit session: DBSession = autoSession): Option[Honor] = withSQL {
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

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Honor as h).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    memberId: Long,
    category: Int,
    name: String,
    set: Boolean)(implicit session: DBSession = autoSession): Honor = {
    val generatedKey = withSQL {
      insert.into(Honor).columns(
        column.memberId,
        column.category,
        column.name,
        column.set
      ).values(
            memberId,
            category,
            name,
            set
          )
    }.updateAndReturnGeneratedKey().apply()

    Honor(
      id = generatedKey,
      memberId = memberId,
      category = category,
      name = name,
      set = set)
  }

  def save(entity: Honor)(implicit session: DBSession = autoSession): Honor = {
    withSQL {
      update(Honor).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.category -> entity.category,
        column.name -> entity.name,
        column.set -> entity.set
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def updateUnset(memberId: Long)(implicit session: DBSession = autoSession): Unit = applyUpdate {
    update(Honor).set(column.set -> false).where.eq(column.memberId, memberId)
  }

  def destroy(entity: Honor)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(Honor).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
