package models

import scalikejdbc._
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
  lazy val b = Basic.syntax("b")
  lazy val x = SubQuery.syntax("x", b.resultName)
  lazy val us = UserSettings.syntax("us")

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

  def findAll(limit: Int = Int.MaxValue, offset: Int = 0)(implicit session: DBSession = Admiral.autoSession): List[Admiral] = withSQL {
    select.from(Admiral as a)
      .limit(limit).offset(offset)
  }.map(Admiral(a)).list().apply()

  def findNewest(limit: Int = Int.MaxValue, offset: Int = 0)(implicit session: DBSession = Admiral.autoSession): List[Admiral] = withSQL {
    select.from(Admiral as a)
      .orderBy(a.created).desc
      .limit(limit).offset(offset)
  }.map(Admiral(a)).list().apply()

  def findAllByLike(q: String, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session:DBSession = Admiral.autoSession): List[AdmiralWithLv] = withSQL {
    select(a.id, a.nickname, a.created, b.lv).from(Admiral as a)
      .innerJoin(Basic as b).on(a.id, b.memberId)
      .where.like(a.nickname, q)
      .and.eq(b.created, sqls"(select MAX(${b.created}) from ${Basic.table} as b where ${a.id} = ${b.memberId})")
  }.map(AdmiralWithLv(a, b)).list().apply()

  def findAllByServer(serverId: Int, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[AdmiralWithLv] =
    withSQL {
      select(a.id, a.nickname, a.created, b.lv).from(Admiral as a)
        .innerJoin(Basic as b).on(a.id, b.memberId)
        .innerJoin(UserSettings as us).on(a.id, us.memberId)
        .where.eq(us.base, serverId)
          .and.eq(b.created, sqls"(select MAX(${b.created}) from ${Basic.table} as b where ${a.id} = ${b.memberId})")
        .orderBy(b.lv).desc
    }.map(AdmiralWithLv(a, b)).list().apply()

  def findAllLvTop(limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = Admiral.autoSession): List[AdmiralWithLv] = withSQL {
    select(a.id, a.nickname, a.created, b.lv).from(Admiral as a)
      .innerJoin(Basic as b).on(a.id, b.memberId)
      .where.eq(b.created, sqls"(select MAX(${b.created}) from ${Basic.table} as b where ${a.id} = ${b.memberId})")
      .orderBy(b.experience).desc
      .limit(limit).offset(offset)
  }.map(AdmiralWithLv(a, b)).list().apply()

  def save(a: Admiral)(implicit session: DBSession = Admiral.autoSession): Admiral = {
    withSQL {
      update(Admiral).set(column.id -> a.id, column.nickname -> a.nickname)
        .where.eq(column.id, a.id)
    }.update()
    a
  }

  def create(a: data.Auth)(implicit session: DBSession = Admiral.autoSession): Admiral = {
    val created = System.currentTimeMillis()
    sql"insert ignore into admiral values (${a.memberId}, ${a.id}, ${a.nickname}, ${created})".update().apply()
    Admiral(a.memberId, a.id, a.nickname, created)
  }
}

case class AdmiralWithLv(id: Long, nickname: String, created: Long, lv: Int)

object AdmiralWithLv {
  def apply(a: SyntaxProvider[Admiral], b: SyntaxProvider[Basic])(rs: WrappedResultSet): AdmiralWithLv = new AdmiralWithLv(
    rs.long(a.id),
    rs.string(a.nickname),
    rs.long(a.created),
    rs.int(b.lv)
  )
}
