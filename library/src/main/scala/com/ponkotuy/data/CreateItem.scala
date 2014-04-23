package com.ponkotuy.data

import org.json4s._
import com.ponkotuy.tool.Pretty
import scala.util.Try

/**
 *
 * @param id : ユーザ毎にUniqueなID？ nullable
 * @param slotitemId : 装備MasterのID？ nullable
 * @author ponkotuy
 * Date; 14/03/05.
 */
case class CreateItem(
    id: Option[Int], slotitemId: Option[Int],
    fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    createFlag: Boolean, shizaiFlag: Boolean, flagship: Int) {
  def materialSummary: String = s"$fuel/$ammo/$steel/$bauxite"
  def summary: String = Pretty(
    Map("成功" -> shizaiFlag, "資材" -> materialSummary) ++
      slotitemId.map(i => Map("ItemID" -> i)).getOrElse(Map())
  )
}

object CreateItem {
  implicit val format = DefaultFormats
  def from(req: Map[String, String], res: JValue, flagship: Int): CreateItem = {
    val slotitem = CreateSlotItem.fromJson(res \ "api_slot_item")
    val JInt(createFlag) = res \ "api_create_flag"
    val JInt(shizaiFlag) = res \ "api_shizai_flag"
    CreateItem(
      slotitem.map(_.id),
      slotitem.map(_.slotitemId),
      fuel = req("api_item1").toInt,
      ammo = req("api_item2").toInt,
      steel = req("api_item3").toInt,
      bauxite = req("api_item4").toInt,
      createFlag.toInt != 0,
      shizaiFlag.toInt != 0,
      flagship
    )
  }
}

case class CreateSlotItem(id: Int, slotitemId: Int)

object CreateSlotItem {
  implicit val formats = DefaultFormats
  def fromJson(obj: JValue): Option[CreateSlotItem] = {
    Try {
      val id = (obj \ "api_id").extract[Int]
      val slotitemId = (obj \ "api_slotitem_id").extract[Int]
      CreateSlotItem(id, slotitemId)
    }.toOption
  }
}
