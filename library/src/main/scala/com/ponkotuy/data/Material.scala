package com.ponkotuy.data

import org.json4s._
import com.ponkotuy.tool.Pretty

/**
 *
 * @param instant : Instant Construction
 * @param develop : Development Material
 * @author ponkotuy
 * Date: 14/02/19.
 */
case class Material(fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    instant: Int, bucket: Int, develop: Int) {
  def summary: String = Pretty(
    Map(
      ("燃料", fuel),
      ("弾薬", ammo),
      ("鉄鋼", steel),
      ("ボーキサイト", bauxite),
      ("高速建造材", instant),
      ("高速修復材", bucket),
      ("開発資材", develop)
    )
  )
}

object Material {
  def fromJson(obj: JValue): Material = {
    implicit def jint2int(jint: JValue) = jint.asInstanceOf[JInt].values.toInt
    val JArray(xs) = obj \ "api_value"
    Material(xs(0), xs(1), xs(2), xs(3), xs(4), xs(5), xs(6))
  }
}
