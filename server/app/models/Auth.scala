package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{DBSession, WrappedResultSet}
import com.ponkotuy.data

/** このツール内でログイン代わりに使うパラメータ
  *
  * @param id nick name id
  */
case class Auth(id: Long, nickname: String, created: Long) {
  def save()(implicit session: DBSession = Auth.autoSession): Auth =
    Auth.save(this)
}

object Auth extends SQLSyntaxSupport[Auth] {
  def apply(x: SyntaxProvider[Auth])(rs: WrappedResultSet): Auth = apply(x.resultName)(rs)
  def apply(x: ResultName[Auth])(rs: WrappedResultSet): Auth = new Auth(
    rs.long(x.id),
    rs.string(x.nickname),
    rs.long(x.created)
  )

  lazy val auth = Auth.syntax("auth")

  def find(id: Long)(implicit session: DBSession = Auth.autoSession): Option[Auth] = {
    withSQL {
      select.from(Auth as auth).where.eq(auth.id, id)
    }.map(Auth(auth)).toOption().apply()
  }

  def findByName(name: String)(implicit session: DBSession = Auth.autoSession): Option[Auth] = {
    withSQL {
      select.from(Auth as auth).where.eq(auth.nickname, name).limit(1)
    }.map(Auth(auth)).toOption().apply()
  }

  def findAllByUser(userId: Long)(implicit session: DBSession = Auth.autoSession): List[Auth] = {
    withSQL {
      select.from(Auth as auth)
        .where.eq(auth.id, userId)
        .orderBy(auth.created).desc
    }.map(Auth(auth)).list().apply()
  }

  def save(a: Auth)(implicit session: DBSession = Auth.autoSession): Auth = {
    withSQL {
      update(Auth).set(column.id -> a.id, column.nickname -> a.nickname)
        .where.eq(column.id, a.id)
    }.update()
    a
  }

  def create(a: data.Auth)(implicit session: DBSession = Auth.autoSession): Auth = {
    val created = System.currentTimeMillis()
    println(created)
    withSQL {
      insert.into(Auth).namedValues(
        column.id -> a.id, column.nickname -> a.nickname, column.created -> created
      )
    }.update().apply()
    Auth(a.id, a.nickname, created)
  }
}
