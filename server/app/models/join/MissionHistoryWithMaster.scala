package models.join

import org.json4s._
import org.json4s.JsonDSL._
import models.db._

/**
 * Created by yosuke on 15/01/16.
 */
case class MissionHistoryWithMaster(history: MissionHistory, master: MasterMission) {
  def missionId = master.id
  def name = master.name
  def mapArea = master.mapArea
  def completeTime = history.completeTime
  def toJson: JObject = ("mapArea" -> mapArea) ~
    ("missionId" -> missionId) ~
    ("name" -> name) ~
    ("completeTime" -> completeTime)
}
