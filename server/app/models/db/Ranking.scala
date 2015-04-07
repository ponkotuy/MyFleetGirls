package models.db

import com.ponkotuy.tool.RankingDiff
import scalikejdbc._

case class Ranking(
  id: Long,
  memberId: Long,
  no: Int,
  rate: Int,
  created: Long) extends RankingDiff {

  def save()(implicit session: DBSession = Ranking.autoSession): Ranking = Ranking.save(this)(session)

  def destroy()(implicit session: DBSession = Ranking.autoSession): Unit = Ranking.destroy(this)(session)
}


object Ranking extends SQLSyntaxSupport[Ranking] {

  override val tableName = "ranking"

  override val columns = Seq("id", "member_id", "no", "rate", "created")

  def apply(r: SyntaxProvider[Ranking])(rs: WrappedResultSet): Ranking = apply(r.resultName)(rs)
  def apply(r: ResultName[Ranking])(rs: WrappedResultSet): Ranking = new Ranking(
    id = rs.get(r.id),
    memberId = rs.get(r.memberId),
    no = rs.get(r.no),
    rate = rs.get(r.rate),
    created = rs.get(r.created)
  )

  val r = Ranking.syntax("r")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Ranking] = {
    withSQL {
      select.from(Ranking as r).where.eq(r.id, id)
    }.map(Ranking(r.resultName)).single().apply()
  }

  def findNewest(memberId: Long)(implicit session: DBSession = autoSession): Option[Ranking] = {
    withSQL {
      select.from(Ranking as r).where.eq(r.memberId, memberId)
          .orderBy(r.created).desc
          .limit(1)
    }.map(Ranking(r)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Ranking] = {
    withSQL(select.from(Ranking as r)).map(Ranking(r.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Ranking as r)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Ranking] = {
    withSQL {
      select.from(Ranking as r).where.append(sqls"${where}")
    }.map(Ranking(r.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Ranking as r).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    memberId: Long,
    no: Int,
    rate: Int,
    created: Long)(implicit session: DBSession = autoSession): Ranking = {
    val generatedKey = withSQL {
      insert.into(Ranking).columns(
        column.memberId,
        column.no,
        column.rate,
        column.created
      ).values(
            memberId,
            no,
            rate,
            created
          )
    }.updateAndReturnGeneratedKey().apply()

    Ranking(
      id = generatedKey,
      memberId = memberId,
      no = no,
      rate = rate,
      created = created)
  }

  def save(entity: Ranking)(implicit session: DBSession = autoSession): Ranking = {
    withSQL {
      update(Ranking).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.no -> entity.no,
        column.rate -> entity.rate,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: Ranking)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(Ranking).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
