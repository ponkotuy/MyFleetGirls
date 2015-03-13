package models.db

import scalikejdbc._
import com.ponkotuy.data

case class AGOProgress(
  memberId: Long,
  var sortie: Int,
  var rankS: Int,
  var reachBoss: Int,
  var winBoss: Int){
}

object AGOProgress extends SQLSyntaxSupport[AGOProgress] {

  override val tableName = "ago_progress"

  override val columns = Seq("member_id", "sortie", "rank_s", "reach_boss", "win_boss")

  override val autoSession = AutoSession

  lazy val p = AGOProgress.syntax("p")

  def apply(x: SyntaxProvider[AGOProgress])(rs: WrappedResultSet): AGOProgress = apply(x.resultName)(rs)
  def apply(x: ResultName[AGOProgress])(rs: WrappedResultSet): AGOProgress = autoConstruct(rs, x)

  def find(memberId: Long)(implicit session: DBSession = autoSession): Option[AGOProgress] = withSQL {
    select.from(AGOProgress as p).where.eq(p.memberId, memberId)
  }.map(AGOProgress(p)).single().apply()

  private def agoAccept(memberId: Long)(f: AGOProgress => Unit): Unit = {
    val acceptedAGO = Quest.find(214, memberId).filter(_.state == 2).isDefined
    if (acceptedAGO) f(find(memberId).getOrElse(AGOProgress(memberId, 0, 0, 0, 0)))
  }

  def maybeUpdate(memberId: Long, mapStart: data.MapStart): Unit = agoAccept(memberId) { (p: AGOProgress) =>
    p.sortie += 1
    save(p)
  }

  def maybeUpdate(memberId: Long, battleResult: data.BattleResult, mapStart: data.MapStart): Unit = agoAccept(memberId) { p =>
    val cellInfo = CellInfo.findOrDefault(mapStart.mapAreaId, mapStart.mapInfoNo, mapStart.no)
    if (cellInfo.boss){
      p.reachBoss += 1
      if (battleResult.win) p.winBoss += 1
    }
    if (battleResult.winRank == "S") p.rankS += 1
    if (cellInfo.boss || battleResult.winRank == "S") save(p)
  }

  private def save(p: AGOProgress)(implicit session: DBSession = autoSession) = {
    val params = Seq(p.memberId, p.sortie, p.rankS, p.reachBoss, p.winBoss).map(x => sqls"$x")
    sql"replace into ago_progress value (${sqls.csv(params:_*)})".execute().apply()
  }

  def deleteAll()(implicit session: DBSession = autoSession) = withSQL {
    delete.from(AGOProgress)
  }.update().apply()
}
