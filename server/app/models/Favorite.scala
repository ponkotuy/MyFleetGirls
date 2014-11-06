package models

import scalikejdbc._

case class Favorite(
  id: Long,
  memberId: Long,
  url: String,
  first: String,
  second: String,
  created: Long) {

  def save()(implicit session: DBSession = Favorite.autoSession): Favorite = Favorite.save(this)(session)

  def destroy()(implicit session: DBSession = Favorite.autoSession): Unit = Favorite.destroy(this)(session)

}


object Favorite extends SQLSyntaxSupport[Favorite] {

  override val tableName = "favorite"

  override val columns = Seq("id", "member_id", "url", "first", "second", "created")

  def apply(f: SyntaxProvider[Favorite])(rs: WrappedResultSet): Favorite = apply(f.resultName)(rs)
  def apply(f: ResultName[Favorite])(rs: WrappedResultSet): Favorite = new Favorite(
    id = rs.get(f.id),
    memberId = rs.get(f.memberId),
    url = rs.get(f.url),
    first = rs.get(f.first),
    second = rs.get(f.second),
    created = rs.get(f.created)
  )

  val f = Favorite.syntax("f")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Favorite] = {
    withSQL {
      select.from(Favorite as f).where.eq(f.id, id)
    }.map(Favorite(f.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Favorite] = {
    withSQL(select.from(Favorite as f)).map(Favorite(f.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Favorite as f)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Favorite] = {
    withSQL {
      select.from(Favorite as f).where.append(sqls"${where}")
    }.map(Favorite(f.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Favorite as f).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def count(url: String)(implicit session: DBSession = autoSession): Long = {
    val (fst, snd) = fstSnd(url)
    countBy(sqls"f.url = $url and f.first = $fst and f.second = $snd")
  }

  def createOrig(
    memberId: Long,
    url: String,
    first: String,
    second: String,
    created: Long)(implicit session: DBSession = autoSession): Favorite = {
    val generatedKey = withSQL {
      insert.into(Favorite).columns(
        column.memberId,
        column.url,
        column.first,
        column.second,
        column.created
      ).values(
          memberId,
          url,
          first,
          second,
          created
        )
    }.updateAndReturnGeneratedKey().apply()

    Favorite(
      id = generatedKey,
      memberId = memberId,
      url = url,
      first = first,
      second = second,
      created = created)
  }

  def create(memberId: Long, url: String)(implicit session: DBSession = autoSession): Unit = {
    val (fst, snd) = fstSnd(url)
    createOrig(memberId, url, fst, snd, System.currentTimeMillis())
  }

  def save(entity: Favorite)(implicit session: DBSession = autoSession): Favorite = {
    withSQL {
      update(Favorite).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.url -> entity.url,
        column.first -> entity.first,
        column.second -> entity.second,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: Favorite)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(Favorite).where.eq(column.id, entity.id)
    }.update().apply()
  }

  def fstSnd(url: String): (String, String) = {
    val parts = url.split('/')
    val first = parts.lift(1).getOrElse("")
    val second = parts.lift(2).getOrElse("")
    (first, second)
  }

}
