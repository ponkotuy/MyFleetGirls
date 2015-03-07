package com.ponkotuy.data

import com.ponkotuy.data.MissionGetItem.RawGetItem
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JValue

/**
 * @author ponkotuy
 * Date: 15/03/07.
 */
case class MissionResult(
    result: Int,
    detail: String,
    getExp: Int,
    getShipExp: List[Int],
    getItem: Option[MissionGetItem],
    fuel: Int,
    bull: Int,
    steel: Int,
    bauxite: Int,
    mapareaName: String,
    missionLv: Int,
    missionName: String,
    shipIds: List[Int],
    useitemFlag: List[Int])

object MissionResult {
  implicit val formats = DefaultFormats
  case class RawMissionResult(
      api_clear_result: Int,
      api_detail: String,
      api_get_exp: Int,
      api_get_exp_lvup: List[List[Int]],
      api_get_item1: RawGetItem,
      api_get_material: List[Int], // [240, 300, 0, 0]
      api_get_ship_exp: List[Int], // [90, 60, 60, 60, 60, 60]
      api_maparea_name: String,
      api_member_exp: Int,
      api_member_lv: Int,
      api_quest_level: Int,
      api_quest_name: String,
      api_ship_id: List[Int],
      api_useitem_flag: List[Int]) {
    def build: Option[MissionResult] = {
      api_get_material match {
        case fuel :: bull :: steel :: bauxite :: _ =>
          Some(
            MissionResult(
              api_clear_result,
              api_detail,
              api_get_exp,
              api_get_ship_exp,
              api_get_item1.build,
              fuel, bull, steel, bauxite,
              api_maparea_name,
              api_quest_level,
              api_quest_name,
              api_ship_id.tail,
              api_useitem_flag
            )
          )
        case _ => None
      }
    }
  }

  def fromJson(obj: JValue): Option[MissionResult] = obj.extractOpt[RawMissionResult].flatMap(_.build)
}

case class MissionGetItem(id: Int, name: String, count: Int)

object MissionGetItem {
  case class RawGetItem(
      api_useitem_id: Int,
      api_useitem_name: Option[String],
      api_useitem_count: Int) {
    def build: Option[MissionGetItem] = {
      api_useitem_name.map { name =>
        MissionGetItem(api_useitem_id, name, api_useitem_count)
      }
    }
  }
}
