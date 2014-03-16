package com.ponkotuy.data.master

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/16.
 */
case class MasterSlotItem(
    id: Int, name: String, typ: List[Int],
    power: Int, torpedo: Int, bomb: Int, antiAir: Int, antiSub: Int, search: Int, hit: Int, length: Int, rare: Int,
    info: String)

object MasterSlotItem {
  implicit val formats = DefaultFormats
  def fromJson(json: JValue): List[MasterSlotItem] = {
    val JArray(xs) = json
    xs.map { x =>
      val id = toInt(x \ "api_id")
      val JString(name) = x \ "api_name"
      val JArray(rawType) = x \ "api_type"
      val typ = rawType.map(toInt)
      val power = toInt(x \ "api_houg")
      val torpedo = toInt(x \ "api_raig")
      val bomb = toInt(x \ "api_baku")
      val antiAir = math.max(toInt(x \ "api_taik"), toInt(x \ "api_tyku"))
      val antiSub = toInt(x \ "api_tais")
      val search = toInt(x \ "api_saku")
      val hit = math.max(toInt(x \ "api_houm"), toInt(x \ "api_raim"))
      val length = toInt(x \ "api_leng")
      val rare = toInt(x \ "api_rare")
      val JString(info) = x \ "api_info"
      MasterSlotItem(id, name, typ, power, torpedo, bomb, antiAir, antiSub, search, hit, length, rare, info)
    }
  }

  def toInt(v: JValue): Int = v.extract[Int]
}
