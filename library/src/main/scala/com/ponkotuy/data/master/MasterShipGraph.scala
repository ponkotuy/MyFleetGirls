package com.ponkotuy.data.master

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/04/26.
 */
case class MasterShipGraph(id: Int, filename: String)

object MasterShipGraph {
  implicit val formats = DefaultFormats
  def fromJson(json: JValue): List[MasterShipGraph] = {
    val JArray(xs) = json
    xs.map { x =>
      val id = (x \ "api_id").extract[Int]
      val JString(filename) = x \ "api_filename"
      MasterShipGraph(id, filename)
    }
  }
}
