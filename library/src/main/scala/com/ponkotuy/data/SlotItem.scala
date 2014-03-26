package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/26.
 */
case class SlotItem(memberId: Long, id: Int, slotitemId: Int, name: String)

object SlotItem {
  implicit val format = DefaultFormats

  def fromJson(obj: JValue): List[SlotItem] = {
    val JArray(xs) = obj
    xs.map { x =>
      val memberId = (x \ "api_member_id").extract[Long]
      val id = (x \ "api_id").extract[Int]
      val slotitemId = (x \ "api_slotitem_id").extract[Int]
      val JString(name) = x \ "api_name"
      SlotItem(memberId, id, slotitemId, name)
    }
  }
}
