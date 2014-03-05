package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date; 14/03/05.
 */
case class CreateItem(
    id: Int, slotitemId: Int,
    fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    createFlag: Boolean, shizaiFlag: Boolean)

object CreateItem {
  def from(req: Map[String, String], res: JValue): CreateItem = {
    val JInt(id) = res \ "api_id"
    val JInt(slotitemId) = res \ "slotitem_id"
    val JInt(createFlag) = res \ "api_create_flag"
    val JInt(shizaiFlag) = res \ "api_shizai_flag"
    CreateItem(
      id.toInt,
      slotitemId.toInt,
      fuel = req("api_item1").toInt,
      ammo = req("api_item2").toInt,
      steel = req("api_item3").toInt,
      bauxite = req("api_item4").toInt,
      createFlag.toInt != 0,
      shizaiFlag.toInt != 0
    )
  }
}
