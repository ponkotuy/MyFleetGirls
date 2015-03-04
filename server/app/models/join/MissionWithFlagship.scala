package models.join

import models.db._
import scalikejdbc._
import controllers.routes

/**
 *
 * @author ponkotuy
 * Date: 14/12/15.
 */
case class MissionWithFlagship(
    memberId: Long,
    missionId: Int,
    missionName: String,
    deckId: Int,
    deckName: String,
    completeTime: Long,
    created: Long,
    flagshipId: Int,
    mapArea: Int) extends UserActivity {
  override def title: String = "任務"
  override def url: String = routes.UserView.dock(memberId).url
  override def message: String = s"${deckName}艦隊が${missionName}から帰還しました"
  override def key: String = s"${memberId}-${deckId}"
}

object MissionWithFlagship {
  def apply(
      m: SyntaxProvider[Mission],
      mm: SyntaxProvider[MasterMission],
      dp: SyntaxProvider[DeckPort],
      s: SyntaxProvider[Ship])(rs: WrappedResultSet): MissionWithFlagship =
    apply(m.resultName, mm.resultName, dp.resultName, s.resultName)(rs)
  def apply(
      m: ResultName[Mission],
      mm: ResultName[MasterMission],
      dp: ResultName[DeckPort],
      s: ResultName[Ship])(rs: WrappedResultSet): MissionWithFlagship =
    new MissionWithFlagship(
      rs.long(m.memberId),
      rs.int(m.number),
      rs.string(mm.name),
      rs.int(m.deckId),
      rs.string(dp.name),
      rs.long(m.completeTime),
      rs.long(m.created),
      rs.int(s.shipId),
      rs.int(mm.mapArea)
    )
}

