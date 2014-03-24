package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/24.
 */
case class MapInfo(id: Int, cleared: Boolean, exbossFlag: Boolean)

object MapInfo {
  implicit val formats = DefaultFormats
  def fromJson(obj: JValue): List[MapInfo] = {
    val JArray(xs) = obj
    xs.map { x =>
      val id = (x \ "api_id").extract[Int]
      val cleared = (x \ "api_cleared").extract[Int] != 0
      val exbossFlag = (x \ "api_exboss_flag").extract[Int] != 0
      MapInfo(id, cleared, exbossFlag)
    }
  }
}
