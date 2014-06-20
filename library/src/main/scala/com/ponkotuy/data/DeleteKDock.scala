package com.ponkotuy.data

import org.json4s._

/**
 * Date: 14/06/21.
 */
case class DeleteKDock(kDockId: Int, shipId: Int)

object DeleteKDock {
  implicit val default = DefaultFormats
  def fromReq(req: Map[String, String], obj: JValue): DeleteKDock = {
    val shipId = (obj \ "api_ship_id").extract[Int]
    val kDockId = req("api_kdock_id").toInt
    DeleteKDock(kDockId, shipId)
  }
}
