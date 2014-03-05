package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{DBSession, WrappedResultSet}
import com.ponkotuy.data

/**
 *
 * @author ponkotuy
 * Date: 2014/03/01.
 */
case class Admiral(id: Long, nicknameId: Long, nickname: String, created: Long) {
  def authentication(auth: data.Auth): Boolean =
    auth.memberId == id && auth.id == nicknameId && auth.nickname == nickname
}

object Admiral extends SQLSyntaxSupport[Admiral] {
  def apply(x: SyntaxProvider[Admiral])(rs: WrappedResultSet): Admiral = apply(x.resultName)(rs)
  def apply(x: ResultName[Admiral])(rs: WrappedResultSet): Admiral = new Admiral(
    rs.long(x.id),
    rs.long(x.nicknameId),
    rs.string(x.nickname),
    rs.long(x.created)
  )

  lazy val a = Admiral.syntax("a")

  def find(id: Long)(implicit session: DBSession = Admiral.autoSession): Option[Admiral] = {
    withSQL {
      select.from(Admiral as a).where.eq(a.id, id)
    }.map(Admiral(a))
      .toOption()
      .apply()
  }

  def findByName(name: String)(implicit session: DBSession = Admiral.autoSession): Option[Admiral] = {
    withSQL {
      select.from(Admiral as a).where.eq(a.nickname, name).limit(1)
    }.map(Admiral(a)).toOption().apply()
  }

  def findAll()(implicit session: DBSession = Admiral.autoSession): List[Admiral] = withSQL {
    select.from(Admiral as a)
  }.map(Admiral(a)).list().apply()

  def findAllByUser(userId: Long)(implicit session: DBSession = Admiral.autoSession): List[Admiral] = {
    withSQL {
      select.from(Admiral as a)
        .where.eq(a.id, userId)
        .orderBy(a.created).desc
    }.map(Admiral(a)).list().apply()
  }

  def save(a: Admiral)(implicit session: DBSession = Admiral.autoSession): Admiral = {
    withSQL {
      update(Admiral).set(column.id -> a.id, column.nickname -> a.nickname)
        .where.eq(column.id, a.id)
    }.update()
    a
  }

  def create(a: data.Auth)(implicit session: DBSession = Admiral.autoSession): Admiral = {
    val created = System.currentTimeMillis()
    withSQL {
      insert.into(Admiral).namedValues(
        column.id -> a.memberId,
        column.nicknameId -> a.id, column.nickname -> a.nickname, column.created -> created
      )
    }.update().apply()
    Admiral(a.memberId, a.id, a.nickname, created)
  }
}
