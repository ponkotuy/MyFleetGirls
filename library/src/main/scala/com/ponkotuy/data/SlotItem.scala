package com.ponkotuy.data

import org.json4s._

/**
 *
 * @param alv 艦載機熟練度
 * @author ponkotuy
 * Date: 14/03/26.
 */
case class SlotItem(id: Int, slotitemId: Int, locked: Boolean, level: Int, alv: Option[Int])

object SlotItem {
  implicit val format = DefaultFormats

  def fromJson(obj: JValue): List[SlotItem] = {
    val JArray(xs) = obj
    xs.flatMap { x =>
      x.extractOpt[RawSlotItem].map(_.build)
    }
  }

  case class RawSlotItem(api_id: Int, api_slotitem_id: Int, api_locked: Int, api_level: Int, api_alv: Option[Int]) {
    def build: SlotItem = SlotItem(api_id, api_slotitem_id, api_locked != 0, api_level, api_alv)
  }
}
