package models.db

import scalikejdbc._
import com.ponkotuy.data

/**
 *
 * @author ponkotuy, lyrical_logical
 * Date: 2014/03/01.
 */
case class Admiral(id: Long, nicknameId: Long, nickname: String, created: Long) {
  def authentication(auth: data.Auth): Boolean =
    auth.memberId == id && auth.id == nicknameId && auth.nickname == nickname
}

object Admiral extends SQLSyntaxSupport[Admiral] {
  def apply(x: SyntaxProvider[Admiral])(rs: WrappedResultSet): Admiral = apply(x.resultName)(rs)
  def apply(x: ResultName[Admiral])(rs: WrappedResultSet): Admiral = autoConstruct(rs, x)

  val a = Admiral.syntax("a")
  val b = Basic.syntax("b")
  val x = SubQuery.syntax("x", b.resultName)
  val us = UserSettings.syntax("us")

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

  def findNewestWithLv(limit: Int = Int.MaxValue, offset: Int = 0)(implicit session: DBSession = Admiral.autoSession): List[AdmiralWithLv] = withSQL {
    select.from(Admiral as a)
      .innerJoin(Basic as b).on(a.id, b.memberId)
      .where(newestBasic)
      .orderBy(a.created).desc
      .limit(limit).offset(offset)
  }.map(AdmiralWithLv(a, b)).list().apply()

  def findAllByLike(q: String, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session:DBSession = Admiral.autoSession): List[AdmiralWithLv] = withSQL {
    select.from(Admiral as a)
      .innerJoin(Basic as b).on(a.id, b.memberId)
      .where.like(a.nickname, q)
        .and.append(newestBasic)
      .orderBy(b.experience).desc
      .limit(limit).offset(offset)
  }.map(AdmiralWithLv(a, b)).list().apply()

  def findAllByServer(serverId: Int, where: SQLSyntax = sqls"true", limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[AdmiralWithLv] =
    withSQL {
      select.from(Admiral as a)
        .innerJoin(Basic as b).on(a.id, b.memberId)
        .innerJoin(UserSettings as us).on(a.id, us.memberId)
        .where.eq(us.base, serverId)
          .and.append(newestBasic)
          .and.append(where)
        .orderBy(b.experience).desc
    }.map(AdmiralWithLv(a, b)).list().apply()

  def findAllLvTop(limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = Admiral.autoSession): List[AdmiralWithLv] = withSQL {
    select.from(Admiral as a)
      .innerJoin(Basic as b).on(a.id, b.memberId)
      .where(newestBasic)
      .orderBy(b.experience).desc
      .limit(limit).offset(offset)
  }.map(AdmiralWithLv(a, b)).list().apply()

  def findAllIn(ids: Seq[Long])(implicit session: DBSession = autoSession): List[Admiral] = withSQL {
    select.from(Admiral as a).where.in(a.id, ids)
  }.map(Admiral(a)(_)).list().apply()

  def findAllInWithLv(ids: Seq[Long])(implicit session: DBSession = autoSession): List[AdmiralWithLv] = withSQL {
    select.from(Admiral as a)
      .innerJoin(Basic as b).on(a.id, b.memberId)
      .where(newestBasic)
      .and.in(a.id, ids)
  }.map(AdmiralWithLv(a, b)).list().apply()

  def countAll()(implicit session: DBSession = autoSession): Long = withSQL {
    select(sqls"count(1)").from(Admiral as a)
  }.map(rs => rs.long(1)).single().apply().get

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

  private def newestBasic =
    sqls.eq(b.created, sqls"(select ${sqls.max(b.created)} from ${Basic.table} as b where ${sqls.eq(a.id, b.memberId)})")
}

case class AdmiralWithLv(id: Long, nickname: String, created: Long, lv: Int, medals: Int) {
  def isGradeA = medals == 1
}

object AdmiralWithLv {
  def apply(a: SyntaxProvider[Admiral], b: SyntaxProvider[Basic])(rs: WrappedResultSet): AdmiralWithLv =
    apply(a.resultName, b.resultName)(rs)
  def apply(a: ResultName[Admiral], b: ResultName[Basic])(rs: WrappedResultSet): AdmiralWithLv = new AdmiralWithLv(
    rs.long(a.id),
    rs.string(a.nickname),
    rs.long(a.created),
    rs.int(b.lv),
    rs.int(b.medals)
  )
}
