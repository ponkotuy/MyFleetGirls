package com.ponkotuy.data.master

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/09.
 */
case class MasterMission(id: Int, mapArea: Int, name: String, time: Int, fuel: Double, ammo: Double)

object MasterMission {
  implicit val formats = DefaultFormats
  def fromJson(obj: JValue): List[MasterMission] = {
    val JArray(xs) = obj
    xs.map { x =>
      val JInt(id) = x \ "api_id"
      val JInt(mapArea) = x \ "api_maparea_id"
      val JString(name) = x \ "api_name"
      val JInt(time) = x \ "api_time"
      val fuel = (x \ "api_use_fuel").extract[Double]
      val ammo = (x \ "api_use_bull").extract[Double]
      MasterMission(id.toInt, mapArea.toInt, name, time.toInt, fuel, ammo)
    }
  }
}
