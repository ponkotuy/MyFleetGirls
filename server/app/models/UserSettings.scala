package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import dat.ShipWithName

case class UserSettings(
  memberId: Long,
  yome: Int) {

  def save()(implicit session: DBSession = UserSettings.autoSession): UserSettings = UserSettings.save(this)(session)

  def destroy()(implicit session: DBSession = UserSettings.autoSession): Unit = UserSettings.destroy(this)(session)

}


object UserSettings extends SQLSyntaxSupport[UserSettings] {

  override val tableName = "user_settings"

  override val columns = Seq("member_id", "yome")

  def apply(us: SyntaxProvider[UserSettings])(rs: WrappedResultSet): UserSettings = apply(us.resultName)(rs)
  def apply(us: ResultName[UserSettings])(rs: WrappedResultSet): UserSettings = new UserSettings(
    memberId = rs.get(us.memberId),
    yome = rs.get(us.yome)
  )

  val us = UserSettings.syntax("us")
  val s = Ship.syntax("s")
  val ms = MasterShipBase.syntax("ms")
  val mst = MasterStype.syntax("mst")

  override val autoSession = AutoSession

  def find(memberId: Long)(implicit session: DBSession = autoSession): Option[UserSettings] = {
    withSQL {
      select.from(UserSettings as us).where.eq(us.memberId, memberId)
    }.map(UserSettings(us.resultName)).single().apply()
  }

  def findYome(memberId: Long)(implicit session: DBSession = autoSession): Option[ShipWithName] = {
    withSQL {
      select.from(UserSettings as us)
        .innerJoin(Ship as s).on(sqls"us.yome = s.id and us.member_id = s.member_id")
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.eq(us.memberId, memberId)
    }.map { rs =>
      val slot = Ship.findSlot(memberId, rs.int(s.resultName.id))
      ShipWithName(Ship(s, slot)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
    }.single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[UserSettings] = {
    withSQL(select.from(UserSettings as us)).map(UserSettings(us.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(UserSettings as us)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[UserSettings] = {
    withSQL {
      select.from(UserSettings as us).where.append(sqls"${where}")
    }.map(UserSettings(us.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(UserSettings as us).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    memberId: Long,
    yome: Int)(implicit session: DBSession = autoSession): UserSettings = {
    withSQL {
      insert.into(UserSettings).columns(
        column.memberId,
        column.yome
      ).values(
          memberId,
          yome
        )
    }.update().apply()

    UserSettings(
      memberId = memberId,
      yome = yome)
  }

  def upsert(memberId: Long, yome: Int)(implicit session: DBSession = autoSession): Unit =
    sql"replace ${UserSettings.table} (${column.memberId}, ${column.yome}) values ($memberId, $yome)".update().apply()

  def save(entity: UserSettings)(implicit session: DBSession = autoSession): UserSettings = {
    withSQL {
      update(UserSettings).set(
        column.memberId -> entity.memberId,
        column.yome -> entity.yome
      ).where.eq(column.memberId, entity.memberId)
    }.update().apply()
    entity
  }

  def destroy(entity: UserSettings)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(UserSettings).where.eq(column.memberId, entity.memberId)
    }.update().apply()
  }

}
