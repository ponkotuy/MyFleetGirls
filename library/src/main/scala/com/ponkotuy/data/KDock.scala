package com.ponkotuy.data

import org.json4s._
import com.ponkotuy.tool.{DateFormatLocal, Pretty}
import java.util.Date

/**
 *
 * @author ponkotuy
 * Date: 14/03/03
 */
case class KDock(
    id: Int, shipId: Int,
    state: Int, completeTime: Long,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int) {
  import KDock._
  def summary: String = Pretty(
    Map("ID" -> id, "ShipID" -> shipId, "建造完了時間" -> df.format(new Date(completeTime)))
  )
}

object KDock {
  def df = DateFormatLocal.default
  def fromJson(obj: JValue): List[KDock] = {
    val JArray(xs) = obj
    implicit def bigint2int(bi: BigInt): Int = bi.toInt
    xs.map { x =>
      val JInt(id) = x \ "api_id"
      val JInt(shipId) = x \ "api_created_ship_id"
      val JInt(state) = x \ "api_state"
      val JInt(completeTime) = x \ "api_complete_time"
      val JInt(fuel) = x \ "api_item1"
      val JInt(ammo) = x \ "api_item2"
      val JInt(steel) = x \ "api_item3"
      val JInt(bauxite) = x \ "api_item4"
      KDock(id, shipId, state, completeTime.toLong, fuel, ammo, steel, bauxite)
    }
  }
}
