package models.db

import scalikejdbc._

case class FleetGroupMember(
  groupId: Long,
  shipId: Int) {

  def save()(implicit session: DBSession = FleetGroupMember.autoSession): FleetGroupMember = FleetGroupMember.save(this)(session)

  def destroy()(implicit session: DBSession = FleetGroupMember.autoSession): Unit = FleetGroupMember.destroy(this)(session)

}


object FleetGroupMember extends SQLSyntaxSupport[FleetGroupMember] {

  override val tableName = "fleet_group_member"

  override val columns = Seq("group_id", "ship_id")

  def apply(fgm: SyntaxProvider[FleetGroupMember])(rs: WrappedResultSet): FleetGroupMember = apply(fgm.resultName)(rs)
  def apply(fgm: ResultName[FleetGroupMember])(rs: WrappedResultSet): FleetGroupMember = autoConstruct(rs, fgm)

  val fgm = FleetGroupMember.syntax("fgm")

  override val autoSession = AutoSession

  def find(groupId: Long, shipId: Int)(implicit session: DBSession = autoSession): Option[FleetGroupMember] = {
    withSQL {
      select.from(FleetGroupMember as fgm).where.eq(fgm.groupId, groupId).and.eq(fgm.shipId, shipId)
    }.map(FleetGroupMember(fgm.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[FleetGroupMember] = {
    withSQL(select.from(FleetGroupMember as fgm)).map(FleetGroupMember(fgm.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(FleetGroupMember as fgm)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[FleetGroupMember] = {
    withSQL {
      select.from(FleetGroupMember as fgm).where.append(sqls"${where}")
    }.map(FleetGroupMember(fgm.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(FleetGroupMember as fgm).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    groupId: Long,
    shipId: Int)(implicit session: DBSession = autoSession): FleetGroupMember = {
    withSQL {
      insert.into(FleetGroupMember).columns(
        column.groupId,
        column.shipId
      ).values(
            groupId,
            shipId
          )
    }.update().apply()

    FleetGroupMember(
      groupId = groupId,
      shipId = shipId)
  }

  def save(entity: FleetGroupMember)(implicit session: DBSession = autoSession): FleetGroupMember = {
    withSQL {
      update(FleetGroupMember).set(
        column.groupId -> entity.groupId,
        column.shipId -> entity.shipId
      ).where.eq(column.groupId, entity.groupId).and.eq(column.shipId, entity.shipId)
    }.update().apply()
    entity
  }

  def destroy(entity: FleetGroupMember)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(FleetGroupMember).where.eq(column.groupId, entity.groupId).and.eq(column.shipId, entity.shipId)
    }.update().apply()
  }

}
