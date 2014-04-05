package com.ponkotuy.data.master

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date:14/04/05.
 */
case class MasterSType(id: Int, sortno: Int, name: String, scnt: Int, kcnt: Int)

object MasterSType {
  implicit val formats = DefaultFormats
  def fromJson(obj: JValue): List[MasterSType] = {
    val JArray(xs) = obj
    xs.map { x =>
      val id = toInt(x \ "api_id")
      val sortno = toInt(x \ "api_sortno")
      val JString(name) = x \ "api_name"
      val scnt = toInt(x \ "api_scnt")
      val kcnt = toInt(x \ "api_kcnt")
      MasterSType(id, sortno, name, scnt, kcnt)
    }
  }

  def toInt(obj: JValue): Int = obj.extract[Int]
}
