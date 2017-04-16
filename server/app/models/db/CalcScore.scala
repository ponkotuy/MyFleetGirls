package models.db

import models.response.ScoreWithSum
import scalikejdbc._
import com.github.nscala_time.time.Imports._
import util.Ymdh

case class CalcScore(
    memberId: Long,
    monthlyExp: Int,
    yearlyExp: Int,
    eo: Int,
    lastEo: Int,
    quest: Int,
    yyyymmddhh: Int,
    created: Long) {

  def save()(implicit session: DBSession = CalcScore.autoSession): CalcScore = CalcScore.save(this)(session)

  def destroy()(implicit session: DBSession = CalcScore.autoSession): Unit = CalcScore.destroy(this)(session)

  def sum: Int = monthlyExp + yearlyExp + eo + lastEo + quest

  def withSum: ScoreWithSum = ScoreWithSum(memberId, sum, monthlyExp, yearlyExp, eo, lastEo, quest, yyyymmddhh, created)

  def ymdh: Ymdh = Ymdh.fromInt(yyyymmddhh)

  def prettyDate: String = if(0 < yyyymmddhh) s"${ymdh.month}月${ymdh.day}日${ymdh.hour}時" else "最新"
}


object CalcScore extends SQLSyntaxSupport[CalcScore] {

  override val tableName = "calc_score"

  override val columns = Seq("member_id", "monthly_exp", "yearly_exp", "eo", "last_eo", "quest", "yyyymmddhh", "created")

  def apply(cs: SyntaxProvider[CalcScore])(rs: WrappedResultSet): CalcScore = autoConstruct(rs, cs)
  def apply(cs: ResultName[CalcScore])(rs: WrappedResultSet): CalcScore = autoConstruct(rs, cs)

  val cs = CalcScore.syntax("cs")

  override val autoSession = AutoSession

  def find(memberId: Long, yyyymmddhh: Int)(implicit session: DBSession = autoSession): Option[CalcScore] = {
    withSQL {
      select.from(CalcScore as cs).where.eq(cs.memberId, memberId).and.eq(cs.yyyymmddhh, yyyymmddhh)
    }.map(CalcScore(cs.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[CalcScore] = {
    withSQL(select.from(CalcScore as cs)).map(CalcScore(cs.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(CalcScore as cs)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[CalcScore] = {
    withSQL {
      select.from(CalcScore as cs).where.append(where)
    }.map(CalcScore(cs.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[CalcScore] = {
    withSQL {
      select.from(CalcScore as cs).where.append(where)
    }.map(CalcScore(cs.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(CalcScore as cs).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
      memberId: Long,
      monthlyExp: Int,
      yearlyExp: Int,
      eo: Int,
      lastEo: Int,
      quest: Int,
      yyyymmddhh: Int,
      created: Long)(implicit session: DBSession = autoSession): CalcScore = {
    withSQL {
      insert.into(CalcScore).columns(
        column.memberId,
        column.monthlyExp,
        column.yearlyExp,
        column.eo,
        column.lastEo,
        column.quest,
        column.yyyymmddhh,
        column.created
      ).values(
        memberId,
        monthlyExp,
        yearlyExp,
        eo,
        lastEo,
        quest,
        yyyymmddhh,
        created
      )
    }.update.apply()

    CalcScore(
      memberId = memberId,
      monthlyExp = monthlyExp,
      yearlyExp = yearlyExp,
      eo = eo,
      lastEo = lastEo,
      quest = quest,
      yyyymmddhh = yyyymmddhh,
      created = created)
  }

  def batchInsert(entities: Seq[CalcScore])(implicit session: DBSession = autoSession): Seq[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'memberId -> entity.memberId,
        'monthlyExp -> entity.monthlyExp,
        'yearlyExp -> entity.yearlyExp,
        'eo -> entity.eo,
        'lastEo -> entity.lastEo,
        'quest -> entity.quest,
        'yyyymmddhh -> entity.yyyymmddhh,
        'created -> entity.created))
        SQL("""insert into calc_score(
        member_id,
        monthly_exp,
        yearly_exp,
        eo,
        last_eo,
        quest,
        yyyymmddhh,
        created
      ) values (
        {memberId},
        {monthlyExp},
        {yearlyExp},
        {eo},
        {lastEo},
        {quest},
        {yyyymmddhh},
        {created}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: CalcScore)(implicit session: DBSession = autoSession): CalcScore = {
    withSQL {
      update(CalcScore).set(
        column.memberId -> entity.memberId,
        column.monthlyExp -> entity.monthlyExp,
        column.yearlyExp -> entity.yearlyExp,
        column.eo -> entity.eo,
        column.lastEo -> entity.lastEo,
        column.quest -> entity.quest,
        column.yyyymmddhh -> entity.yyyymmddhh,
        column.created -> entity.created
      ).where.eq(column.memberId, entity.memberId).and.eq(column.yyyymmddhh, entity.yyyymmddhh)
    }.update.apply()
    entity
  }

  def destroy(entity: CalcScore)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(CalcScore).where.eq(column.memberId, entity.memberId).and.eq(column.yyyymmddhh, entity.yyyymmddhh)
    }.update.apply()
  }

  val zero = CalcScore(0L, 0, 0, 0, 0, 0, 0, 0L)

}
