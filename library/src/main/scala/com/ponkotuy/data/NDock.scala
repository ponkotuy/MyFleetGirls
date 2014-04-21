package com.ponkotuy.data

import org.json4s._
import com.ponkotuy.tool.{DateFormatLocal, Pretty}
import java.util.Date

/**
 *
 * ID is not unique. ID is Dock Number(1-4).
 *
 * @author ponkotuy
 * Date: 2014/03/01.
 */
case class NDock(id: Int, memberId: Long, shipId: Int, completeTime: Long) {
  import NDock._
  def summary: String = Pretty(
    Map("ID" -> id, "ShipID" -> shipId, "入渠完了時間" -> df.format(new Date(completeTime)))
  )
}

object NDock {
  def df = DateFormatLocal.default
  def fromJson(json: JValue): List[NDock] = {
    val JArray(xs) = json
    xs.map { x =>
      val JInt(id) = x \ "api_id"
      val JInt(memberId) = x \ "api_member_id"
      val JInt(shipId) = x \ "api_ship_id"
      val JInt(completeTime) = x \ "api_complete_time"
      NDock(id.toInt, memberId.toLong, shipId.toInt, completeTime.toLong)
    }
  }
}
