package models.db

import scalikejdbc._

case class FleetGroup(
  id: Long,
  name: String) {

  def save()(implicit session: DBSession = FleetGroup.autoSession): FleetGroup = FleetGroup.save(this)(session)

  def destroy()(implicit session: DBSession = FleetGroup.autoSession): Unit = FleetGroup.destroy(this)(session)

}


object FleetGroup extends SQLSyntaxSupport[FleetGroup] {

  override val tableName = "fleet_group"

  override val columns = Seq("id", "name")

  def apply(fg: SyntaxProvider[FleetGroup])(rs: WrappedResultSet): FleetGroup = autoConstruct(rs, fg)
  def apply(fg: ResultName[FleetGroup])(rs: WrappedResultSet): FleetGroup = autoConstruct(rs, fg)

  val fg = FleetGroup.syntax("fg")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[FleetGroup] = {
    withSQL {
      select.from(FleetGroup as fg).where.eq(fg.id, id)
    }.map(FleetGroup(fg.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[FleetGroup] = {
    withSQL(select.from(FleetGroup as fg)).map(FleetGroup(fg.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(FleetGroup as fg)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[FleetGroup] = {
    withSQL {
      select.from(FleetGroup as fg).where.append(sqls"${where}")
    }.map(FleetGroup(fg.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(FleetGroup as fg).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    name: String)(implicit session: DBSession = autoSession): FleetGroup = {
    val generatedKey = withSQL {
      insert.into(FleetGroup).columns(
        column.name
      ).values(name)

    }.updateAndReturnGeneratedKey().apply()

    FleetGroup(
      id = generatedKey,
      name = name)
  }

  def save(entity: FleetGroup)(implicit session: DBSession = autoSession): FleetGroup = {
    withSQL {
      update(FleetGroup).set(
        column.id -> entity.id,
        column.name -> entity.name
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: FleetGroup)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(FleetGroup).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
