package models.db

import scalikejdbc._

case class MyFleetAuth(
  id: Long,
  hash: Array[Byte],
  salt: Array[Byte],
  created: Long) {

  def save()(implicit session: DBSession = MyFleetAuth.autoSession): MyFleetAuth = MyFleetAuth.save(this)(session)

  def destroy()(implicit session: DBSession = MyFleetAuth.autoSession): Unit = MyFleetAuth.destroy(this)(session)

}


object MyFleetAuth extends SQLSyntaxSupport[MyFleetAuth] {

  override val tableName = "myfleet_auth"

  override val columns = Seq("id", "hash", "salt", "created")

  def apply(mfa: SyntaxProvider[MyFleetAuth])(rs: WrappedResultSet): MyFleetAuth = apply(mfa.resultName)(rs)
  def apply(mfa: ResultName[MyFleetAuth])(rs: WrappedResultSet): MyFleetAuth = new MyFleetAuth(
    id = rs.get(mfa.id),
    hash = rs.get(mfa.hash),
    salt = rs.get(mfa.salt),
    created = rs.get(mfa.created)
  )

  val mfa = MyFleetAuth.syntax("mfa")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[MyFleetAuth] = {
    withSQL {
      select.from(MyFleetAuth as mfa).where.eq(mfa.id, id)
    }.map(MyFleetAuth(mfa.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MyFleetAuth] = {
    withSQL(select.from(MyFleetAuth as mfa)).map(MyFleetAuth(mfa.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MyFleetAuth as mfa)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MyFleetAuth] = {
    withSQL {
      select.from(MyFleetAuth as mfa).where.append(sqls"${where}")
    }.map(MyFleetAuth(mfa.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MyFleetAuth as mfa).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    id: Long,
    hash: Array[Byte],
    salt: Array[Byte],
    created: Long)(implicit session: DBSession = autoSession): MyFleetAuth = {
    withSQL {
      insert.into(MyFleetAuth).columns(
        column.id,
        column.hash,
        column.salt,
        column.created
      ).values(
          id,
          hash,
          salt,
          created
        )
    }.update().apply()

    MyFleetAuth(
      id = id,
      hash = hash,
      salt = salt,
      created = created)
  }

  def save(entity: MyFleetAuth)(implicit session: DBSession = autoSession): MyFleetAuth = {
    withSQL {
      update(MyFleetAuth).set(
        column.id -> entity.id,
        column.hash -> entity.hash,
        column.salt -> entity.salt,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: MyFleetAuth)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MyFleetAuth).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
