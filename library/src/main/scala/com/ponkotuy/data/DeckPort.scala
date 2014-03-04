package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/03
 */
case class DeckPort(id: Int, memberId: Long, name: String, mission: Option[Mission], ships: List[Int])
case class Mission(page: Int, number: Int, completeTime: Long)

object DeckPort {
  def fromJson(obj: JValue): List[DeckPort] = {
    val JArray(xs) = obj
    xs.map { x =>
      val JInt(id) = x \ "api_id"
      val JInt(memberId) = x \ "api_member_id"
      val JString(name) = x \ "api_name"
      val JArray(List(JInt(page), JInt(number), JInt(completeTime), _)) = x \ "api_mission"
      val mission: Option[Mission] = if(page == BigInt(0) && number == BigInt(0)) {
        None
      } else {
        Some(Mission(page.toInt, number.toInt, completeTime.toLong))
      }
      val JArray(shipsRaw) = x \ "api_ship"
      val ships = shipsRaw.map { x =>
        val JInt(i) = x
        i.toInt
      }.filter(_ >= 0)
      DeckPort(id.toInt, memberId.toLong, name, mission, ships)
    }
  }
}
