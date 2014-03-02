package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/03
 */
case class KDock(
    id: Int, memberId: Long, shipId: Int,
    state: Int, completeTime: Long,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int)

object KDock {
  def fromJson(obj: JValue): List[KDock] = {
    val JArray(xs) = obj
    implicit def bigint2int(bi: BigInt): Int = bi.toInt
    xs.map { x =>
      val JInt(id) = x \ "api_id"
      val JInt(memberId) = x \ "api_member_id"
      val JInt(shipId) = x \ "api_created_ship_id"
      val JInt(state) = x \ "api_state"
      val JInt(completeTime) = x \ "api_complete_time"
      val JInt(fuel) = x \ "api_item1"
      val JInt(ammo) = x \ "api_item2"
      val JInt(steel) = x \ "api_item3"
      val JInt(bauxite) = x \ "api_item4"
      KDock(id, memberId.toLong, shipId, state, completeTime.toLong, fuel, ammo, steel, bauxite)
    }
  }
}
