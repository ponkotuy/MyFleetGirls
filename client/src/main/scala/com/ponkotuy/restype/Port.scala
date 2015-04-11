package com.ponkotuy.restype

import org.json4s.JValue

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object Port extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$Api/api_port/port\\z".r

  override def postables(req: Req, obj: JValue): Seq[Result] = {
    Ship2.postables(req, obj \ "api_ship") ++
        Material.postables(req, obj \ "api_material") ++
        NDock.postables(req, obj \ "api_ndock") ++
        DeckPort.postables(req, obj \ "api_deck_port") ++
        Basic.postables(req, obj \ "api_basic")
  }
}
