package com.ponkotuy.data

import org.json4s._

/**
 *
 * @param category: 1編成 2出撃 3演習 4遠征 5補給/入渠 6工廠 7改装
 * @param typ: 1Once 2Daily 3Weekly
 * @param state: 1未選択 2選択済み 3達成
 * @param progressFlag: 1 50%以上達成 2 80%以上達成
 * @author ponkotuy
 * Date: 14/05/10.
 */
case class Quest(
    no: Int,
    category: Int,
    typ: Int,
    state: Int,
    title: String,
    detail: String,
    material: QuestMaterial,
    bonus: Boolean,
    progressFlag: Int)

object Quest {
  implicit val formats = DefaultFormats

  def fromJson(obj: JValue): Quest = {
    val no = (obj \ "api_no").extract[Int]
    val category = (obj \ "api_category").extract[Int]
    val typ = (obj \ "api_type").extract[Int]
    val state = (obj \ "api_state").extract[Int]
    val JString(title) = obj \ "api_title"
    val JString(detail) = obj \ "api_detail"
    val JArray(rawMaterial) = obj \ "api_get_material"
    val material = QuestMaterial.fromJson(rawMaterial)
    val bonus = (obj \ "api_bonus_flag").extract[Int] != 0
    val progressFlag = (obj \ "api_progress_flag").extract[Int]
    Quest(no, category, typ, state, title, detail, material, bonus, progressFlag)
  }
}

case class QuestMaterial(fuel: Int, ammo: Int, steel: Int, bauxite: Int)

object QuestMaterial {
  implicit val formats = DefaultFormats

  def fromJson(xs: List[JValue]): QuestMaterial = {
    val List(fuel, ammo, steel, bauxite) = xs.map(_.extract[Int])
    QuestMaterial(fuel, ammo, steel, bauxite)
  }
}

object QuestList {
  def fromJson(obj: JValue): List[Quest] = {
    val JArray(xs) = obj \ "api_list"
    xs.filter(_.isInstanceOf[JObject]).map(Quest.fromJson)
  }
}
