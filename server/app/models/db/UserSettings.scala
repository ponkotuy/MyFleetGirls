package models.db

import com.ponkotuy.value.KCServer
import models.join.ShipWithName
import scalikejdbc._

case class UserSettings(
  memberId: Long,
  base: Option[Int]) {

  def save()(implicit session: DBSession = UserSettings.autoSession): UserSettings = UserSettings.save(this)(session)

  def destroy()(implicit session: DBSession = UserSettings.autoSession): Unit = UserSettings.destroy(this)(session)

  def server: Option[KCServer] = base.flatMap(KCServer.fromNumber)

}


object UserSettings extends SQLSyntaxSupport[UserSettings] {

  override val tableName = "user_settings"

  override val columns = Seq("member_id", "base")

  def apply(us: SyntaxProvider[UserSettings])(rs: WrappedResultSet): UserSettings = apply(us.resultName)(rs)
  def apply(us: ResultName[UserSettings])(rs: WrappedResultSet): UserSettings = autoConstruct(rs, us)

  lazy val us = UserSettings.syntax("us")
  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")
  lazy val mst = MasterStype.syntax("mst")
  lazy val mss = MasterShipSpecs.syntax("mss")

  override val autoSession = AutoSession

  def find(memberId: Long)(implicit session: DBSession = autoSession): Option[UserSettings] = {
    withSQL {
      select.from(UserSettings as us).where.eq(us.memberId, memberId)
    }.map(UserSettings(us.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[UserSettings] = {
    withSQL(select.from(UserSettings as us)).map(UserSettings(us.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(UserSettings as us)).map(rs => rs.long(1)).single().apply().get
  }

  def countAllByBase()(implicit session: DBSession = autoSession): Seq[(UserSettings, Long)] = {
    withSQL {
      select(us.resultAll, sqls"count(base) as cnt").from(UserSettings as us)
        .groupBy(us.base)
    }.map { rs =>
      UserSettings(us)(rs) -> rs.long("cnt")
    }.list().apply()
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
    base: Option[Int])(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      insert.into(UserSettings).columns(
        column.memberId,
        column.base
      ).values(
          memberId,
          base
        )
    }.update().apply()
  }

  def setBase(memberId: Long, base: Int)(implicit session: DBSession = autoSession): Unit =
    sql"insert into ${UserSettings.table} (${column.memberId}, ${column.base}) values ($memberId, $base) on duplicate key update ${column.base}=${base}".update().apply()

  def save(entity: UserSettings)(implicit session: DBSession = autoSession): UserSettings = {
    withSQL {
      update(UserSettings).set(
        column.memberId -> entity.memberId,
        column.base -> entity.base
      ).where.eq(column.memberId, entity.memberId)
    }.update().apply()
    entity
  }

  def destroy(entity: UserSettings)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(UserSettings).where.eq(column.memberId, entity.memberId)
    }.update().apply()
  }

  def empty(memberId: Long): UserSettings = UserSettings(memberId, None)

}
