package models

import scalikejdbc.async._
import scalikejdbc.async.FutureImplicits._
import scalikejdbc.SQLInterpolation._
import scalikejdbc.WrappedResultSet
import scala.concurrent.Future
import com.ponkotuy.data

/** このツール内でログイン代わりに使うパラメータ
  *
  * @param id nick name id
  */
case class Auth(id: Long, nickname: String, created: Long) extends ShortenedNames {
  def save()(implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Auth] =
    Auth.save(this)
}

object Auth extends SQLSyntaxSupport[Auth] with ShortenedNames {
  def apply(x: SyntaxProvider[Auth])(rs: WrappedResultSet): Auth = apply(x.resultName)(rs)
  def apply(x: ResultName[Auth])(rs: WrappedResultSet): Auth = new Auth(
    rs.long(x.id),
    rs.string(x.nickname),
    rs.long(x.created)
  )

  lazy val auth = Auth.syntax("auth")

  def find(id: Long)(
    implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Option[Auth]] = {
    println(session)
    withSQL {
      select.from(Auth as auth).where.eq(auth.id, id)
    }.map(Auth(auth))
  }

  def save(a: Auth)(
    implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Auth] = withSQL {
    update(Auth).set(column.id -> a.id, column.nickname -> a.nickname)
      .where.eq(column.id, a.id)
  }.update().future.map(_ => a)

  def create(a: data.Auth)(
      implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Auth] = {
    val created = System.currentTimeMillis()
    withSQL {
      insert.into(Auth).namedValues(
        column.id -> a.id, column.nickname -> a.nickname, column.created -> created
      )
    }.update().future().map { _ =>
      Auth(a.id, a.nickname, created)
    }
  }
}
