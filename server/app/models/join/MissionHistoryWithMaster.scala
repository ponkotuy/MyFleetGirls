package models.join

import org.json4s._
import org.json4s.JsonDSL._
import models.db._

/**
 * @author ponkotuy
 * Date: 15/01/16.
 */
case class MissionHistoryWithMaster(history: MissionHistory, master: MasterMission) {
  def missionId = history.id
  def number = history.number
  def name = master.name
  def mapArea = master.mapArea
  def completeTime = history.completeTime

  def toJson: JObject = ("mapArea" -> mapArea) ~
    ("missionId" -> missionId) ~
    ("number" -> number) ~
    ("name" -> name) ~
    ("completeTime" -> completeTime)

  def toJsonWithShip(ships: Seq[MissionHistoryShipWithMaster]): JObject = {
    val xs = ships.filter(_.missionId == missionId)
    toJson ~ ("ships" -> xs.map(_.toJson))
  }
}
