package com.ponkotuy.data

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

/**
 *
 * @param instant : Instant Construction
 * @param develop : Development Material
 * @author ponkotuy
 * Date: 14/02/19.
 */
case class Material(memberId: Long, fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    instant: Int, bucket: Int, develop: Int)

object Material {
  def fromJson(obj: JValue): Material = {
    implicit def jint2int(jint: JValue) = jint.asInstanceOf[JInt].values.toInt
    val JArray(xs) = obj \ "api_value"
    val JInt(memberId) = obj \ "api_member_id"
    Material(memberId.toLong, xs(0), xs(1), xs(2), xs(3), xs(4), xs(5), xs(6))
  }
}
