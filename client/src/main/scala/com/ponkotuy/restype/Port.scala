package com.ponkotuy.restype

import com.ponkotuy.parser.Query

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object Port extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$Api/api_port/port\\z".r

  override def postables(q: Query): Seq[Result] = {
    val obj = q.obj
    Ship2.postablesFromObj(obj \ "api_ship") ++
      Material.postablesFromObj(obj \ "api_material") ++
      NDock.postablesFromObj(obj \ "api_ndock") ++
      DeckPort.postablesFromObj(obj \ "api_deck_port") ++
      Basic.postablesFromObj(obj \ "api_basic", q.uri)
  }
}
