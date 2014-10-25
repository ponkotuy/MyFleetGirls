package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/26.
 */
case class SlotItem(id: Int, slotitemId: Int, locked: Boolean, level: Int)

object SlotItem {
  implicit val format = DefaultFormats

  def fromJson(obj: JValue): List[SlotItem] = {
    val JArray(xs) = obj
    xs.map { x =>
      val id = (x \ "api_id").extract[Int]
      val slotitemId = (x \ "api_slotitem_id").extract[Int]
      val locked = (x \ "api_locked").extract[Int] != 0
      val level = (x \ "api_level").extract[Int]
      SlotItem(id, slotitemId, locked, level)
    }
  }
}
