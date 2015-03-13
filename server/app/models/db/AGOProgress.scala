package models.db

import scalikejdbc._
import com.ponkotuy.data

case class AGOProgress(
    memberId: Long,
    sortie: Int,
    rankS: Int,
    reachBoss: Int,
    winBoss: Int){

  def save()(implicit session: DBSession = AGOProgress.autoSession): Unit = AGOProgress.save(this)(session)
}

object AGOProgress extends SQLSyntaxSupport[AGOProgress] {

  override val tableName = "ago_progress"

  override val columns = Seq("member_id", "sortie", "rank_s", "reach_boss", "win_boss")

  val AgoQuest = 214

  val ap = AGOProgress.syntax("ap")

  def apply(x: SyntaxProvider[AGOProgress])(rs: WrappedResultSet): AGOProgress = apply(x.resultName)(rs)

  def apply(x: ResultName[AGOProgress])(rs: WrappedResultSet): AGOProgress = autoConstruct(rs, x)

  def find(memberId: Long)(implicit session: DBSession = autoSession): Option[AGOProgress] = withSQL {
    select.from(AGOProgress as ap).where.eq(ap.memberId, memberId)
  }.map(AGOProgress(ap)).single().apply()

  private def agoAccept(memberId: Long)(f: AGOProgress => Unit): Unit = {
    val acceptedAGO = Quest.find(AgoQuest, memberId).exists(2 <= _.state)
    if(acceptedAGO) f(find(memberId).getOrElse(AGOProgress(memberId, 0, 0, 0, 0)))
  }

  def maybeUpdate(memberId: Long, mapStart: data.MapStart): Unit = agoAccept(memberId) { p =>
    p.copy(sortie = p.sortie + 1).save()
  }

  def maybeUpdate(memberId: Long, battleResult: data.BattleResult, mapStart: data.MapStart): Unit = agoAccept(memberId) { p =>
    val cellInfo = CellInfo.find(mapStart.mapAreaId, mapStart.mapInfoNo, mapStart.no)
    if(cellInfo.exists(_.boss)) {
      p.copy(reachBoss = p.reachBoss + 1)
      if(battleResult.win) p.copy(winBoss = p.winBoss + 1)
    }
    if(battleResult.winRank == "S") p.copy(rankS = p.rankS + 1)
    if(cellInfo.exists(_.boss) || battleResult.winRank == "S") p.save()
  }

  private def save(p: AGOProgress)(implicit session: DBSession = autoSession): Unit = {
    val params = Seq(p.memberId, p.sortie, p.rankS, p.reachBoss, p.winBoss).map(x => sqls"$x")
    sql"replace into ago_progress value (${sqls.csv(params:_*)})".execute().apply()
  }

  def deleteAll()(implicit session: DBSession = autoSession) = withSQL {
    delete.from(AGOProgress)
  }.update().apply()
}
