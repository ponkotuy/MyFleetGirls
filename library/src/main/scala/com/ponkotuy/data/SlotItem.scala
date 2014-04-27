package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/26.
 */
case class SlotItem(id: Int, slotitemId: Int)

object SlotItem {
  implicit val format = DefaultFormats

  def fromJson(obj: JValue): List[SlotItem] = {
    val JArray(xs) = obj
    xs.map { x =>
      val id = (x \ "api_id").extract[Int]
      val slotitemId = (x \ "api_slotitem_id").extract[Int]
      SlotItem(id, slotitemId)
    }
  }
}
