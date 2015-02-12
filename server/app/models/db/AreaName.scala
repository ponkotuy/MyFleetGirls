package models.db

import scalikejdbc._
import scala.collection.mutable

case class AreaName(
  id: Int,
  viewName: String,
  name: String) {

  def save()(implicit session: DBSession = AreaName.autoSession): AreaName = AreaName.save(this)(session)

  def destroy()(implicit session: DBSession = AreaName.autoSession): Unit = AreaName.destroy(this)(session)

}


object AreaName extends SQLSyntaxSupport[AreaName] {

  override val tableName = "area_name"

  override val columns = Seq("id", "view_name", "name")

  private[this] val cache = mutable.Map[Int, Option[AreaName]]()

  def apply(an: SyntaxProvider[AreaName])(rs: WrappedResultSet): AreaName = apply(an.resultName)(rs)
  def apply(an: ResultName[AreaName])(rs: WrappedResultSet): AreaName = new AreaName(
    id = rs.get(an.id),
    viewName = rs.get(an.viewName),
    name = rs.get(an.name)
  )

  val an = AreaName.syntax("an")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[AreaName] = {
    cache.getOrElseUpdate(id, findFromDB(id))
  }

  def findFromDB(id: Int)(implicit session: DBSession = autoSession): Option[AreaName] = {
    withSQL {
      select.from(AreaName as an).where.eq(an.id, id)
    }.map(AreaName(an.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[AreaName] = {
    withSQL(select.from(AreaName as an)).map(AreaName(an.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(AreaName as an)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[AreaName] = {
    withSQL {
      select.from(AreaName as an).where.append(sqls"${where}")
    }.map(AreaName(an.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(AreaName as an).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    id: Int,
    viewName: String,
    name: String)(implicit session: DBSession = autoSession): AreaName = {
    withSQL {
      insert.into(AreaName).columns(
        column.id,
        column.viewName,
        column.name
      ).values(
          id,
          viewName,
          name
        )
    }.update().apply()

    AreaName(
      id = id,
      viewName = viewName,
      name = name)
  }

  def save(entity: AreaName)(implicit session: DBSession = autoSession): AreaName = {
    withSQL {
      update(AreaName).set(
        column.id -> entity.id,
        column.viewName -> entity.viewName,
        column.name -> entity.name
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: AreaName)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(AreaName).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
