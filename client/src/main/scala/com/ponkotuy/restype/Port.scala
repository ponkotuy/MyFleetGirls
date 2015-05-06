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
    Ship2.postablesFromObj(q.obj \ "api_ship") ++
        Material.postablesFromObj(q.obj \ "api_material") ++
        NDock.postablesFromObj(q.obj \ "api_ndock") ++
        DeckPort.postablesFromObj(q.obj \ "api_deck_port") ++
        Basic.postablesFromObj(q.obj \ "api_basic")
  }
}
