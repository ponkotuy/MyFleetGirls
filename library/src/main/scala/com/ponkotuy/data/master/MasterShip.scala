package com.ponkotuy.data.master

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
case class MasterShip(id: Int, name: String, yomi: String)

object MasterShip {
  def fromJson(json: JValue): List[MasterShip] = {
    val JArray(xs) = json
    xs.map { x =>
      val JInt(id) = x \ "api_id"
      val JString(name) = x \ "api_name"
      val JString(yomi) = x \  "api_yomi"
      MasterShip(id.toInt, name, yomi)
    }
  }
}
