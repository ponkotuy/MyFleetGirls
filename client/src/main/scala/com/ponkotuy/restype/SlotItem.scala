package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.JValue
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object SlotItem extends ResType {
  import ResType._
  override def regexp: Regex = s"\\A$GetMember/slot_item\\z".r

  override def postables(q: Query): Seq[Result] = fromJson(q.obj)

  def fromJson(json: JValue): Seq[Result] = {
    val items = data.SlotItem.fromJson(json)
    NormalPostable("/slotitem", write(items), 1, s"所持装備数 -> ${items.size}") :: Nil
  }
}
