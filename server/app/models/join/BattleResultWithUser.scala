package models.join

import models.db.{Admiral, BattleResult}
import scalikejdbc._

/**
 * Date: 14/07/17.
 */
case class BattleResultWithUser(battleResult: BattleResult, admiral: Admiral) extends Activity {
  def areaId: Int = battleResult.areaId
  def infoNo: Int = battleResult.infoNo
  def cell: Int = battleResult.cell
  def getShipId: Option[Int] = battleResult.getShipId
  def getShipName: Option[String] = battleResult.getShipName

  def stage: String = s"${areaId}-${infoNo}-${cell}"

  override def title: String = "ドロップ"
  override def created: Long = battleResult.created
  override def url: String = controllers.routes.UserView.navalBattle(battleResult.memberId).url + "#dropOnly=true"
  override def message: String = getShipName.map { sName =>
    s"${nickname}提督が${stage}で${sName}を入手しました"
  }.getOrElse("")
  override def id: String = battleResult.id.toString
}

object BattleResultWithUser {
  def apply(br: SyntaxProvider[BattleResult], a: SyntaxProvider[Admiral])(
      rs: WrappedResultSet): BattleResultWithUser = {
    BattleResultWithUser(BattleResult(br.resultName)(rs), Admiral(a)(rs))
  }
}
