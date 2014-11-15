package models.db

import java.util.UUID

import scalikejdbc._

case class Session(
  uuidMost: Long,
  uuidLeast: Long,
  memberId: Long,
  created: Long) {

  def save()(implicit session: DBSession = Session.autoSession): Session = Session.save(this)(session)

  def destroy()(implicit session: DBSession = Session.autoSession): Unit = Session.destroy(this)(session)

  lazy val uuid = new UUID(uuidMost, uuidLeast)

}


object Session extends SQLSyntaxSupport[Session] {

  override val tableName = "session"

  override val columns = Seq("uuid_most", "uuid_least", "member_id", "created")

  def apply(s: SyntaxProvider[Session])(rs: WrappedResultSet): Session = apply(s.resultName)(rs)
  def apply(s: ResultName[Session])(rs: WrappedResultSet): Session = new Session(
    uuidMost = rs.get(s.uuidMost),
    uuidLeast = rs.get(s.uuidLeast),
    memberId = rs.get(s.memberId),
    created = rs.get(s.created)
  )

  val s = Session.syntax("s")

  override val autoSession = AutoSession

  def findByUUID(uuid: UUID)(implicit session: DBSession = autoSession): Option[Session] =
    find(uuid.getLeastSignificantBits, uuid.getMostSignificantBits)

  def find(uuidLeast: Long, uuidMost: Long)(implicit session: DBSession = autoSession): Option[Session] = {
    withSQL {
      select.from(Session as s).where.eq(s.uuidLeast, uuidLeast).and.eq(s.uuidMost, uuidMost)
    }.map(Session(s.resultName)).single().apply()
  }

  def findByUser(memberId: Long)(implicit session: DBSession = autoSession): Option[Session] = withSQL {
    select.from(Session as s).where.eq(s.memberId, memberId)
  }.map(Session(s.resultName)).single().apply()

  def findAll()(implicit session: DBSession = autoSession): List[Session] = {
    withSQL(select.from(Session as s)).map(Session(s.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Session as s)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Session] = {
    withSQL {
      select.from(Session as s).where.append(sqls"${where}")
    }.map(Session(s.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Session as s).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def createByUUID(uuid: UUID, memberId: Long)(implicit session: DBSession = autoSession): Session =
    create(uuid.getMostSignificantBits, uuid.getLeastSignificantBits, memberId, System.currentTimeMillis())

  def create(
    uuidMost: Long,
    uuidLeast: Long,
    memberId: Long,
    created: Long)(implicit session: DBSession = autoSession): Session = {
    withSQL {
      insert.into(Session).columns(
        column.uuidMost,
        column.uuidLeast,
        column.memberId,
        column.created
      ).values(
          uuidMost,
          uuidLeast,
          memberId,
          created
        )
    }.update().apply()

    Session(
      uuidMost = uuidMost,
      uuidLeast = uuidLeast,
      memberId = memberId,
      created = created)
  }

  def save(entity: Session)(implicit session: DBSession = autoSession): Session = {
    withSQL {
      update(Session).set(
        column.uuidMost -> entity.uuidMost,
        column.uuidLeast -> entity.uuidLeast,
        column.memberId -> entity.memberId,
        column.created -> entity.created
      ).where.eq(column.uuidLeast, entity.uuidLeast).and.eq(column.uuidMost, entity.uuidMost)
    }.update().apply()
    entity
  }

  def destroy(entity: Session)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(Session).where.eq(column.uuidLeast, entity.uuidLeast).and.eq(column.uuidMost, entity.uuidMost)
    }.update().apply()
  }

}
