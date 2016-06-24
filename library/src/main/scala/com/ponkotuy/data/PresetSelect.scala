package com.ponkotuy.data

import org.json4s.{DefaultFormats, JValue}

case class PresetSelect(
    memberId: Long,
    id: Int,
    name: String,
    mission: List[Int],
    flagship: Int,
    ship: List[Int])

object PresetSelect {
  implicit val formats = DefaultFormats
  def fromJson(obj: JValue): Option[PresetSelect] = obj.extractOpt[RawPresetSelect].map(_.build)

  case class RawPresetSelect(
      api_member_id: Long,
      api_id: Int,
      api_name: String,
      api_name_id: String, // 空文字列の可能性がある。面倒くさいので使わないことにする
      api_mission: List[Int],
      api_flagship: String,
      api_ship: List[Int]) {
    def build: PresetSelect =
      PresetSelect(api_member_id, api_id, api_name, api_mission, api_flagship.toInt, api_ship.filter(0 <= _))
  }
}
