package com.ponkotuy.data

import org.json4s._
import com.ponkotuy.tool.{DateFormatLocal, Pretty}
import java.util.Date

/**
 *
 * @author ponkotuy
 * Date: 14/03/03
 */
case class DeckPort(id: Int, name: String, mission: Option[Mission], ships: List[Int]) {
  def summary: String = Pretty(
    Map(
      "ID" -> id,
      "名前" -> name,
      "艦娘" -> ships.mkString("(", ", ", ")"),
      "遠征" -> mission.map(_.summary).getOrElse("")
    )
  )
}
case class Mission(page: Int, number: Int, completeTime: Long) {
  import DeckPort._
  def summary: String = {
    val content = Pretty(
      Map("ID" -> s"$page-$number", "遠征完了時間" -> df.format(new Date(completeTime)))
    )
    s"(${content})"
  }
}

object DeckPort {
  def df = DateFormatLocal.default
  def fromJson(obj: JValue): List[DeckPort] = {
    val JArray(xs) = obj
    xs.map { x =>
      val JInt(id) = x \ "api_id"
      val JString(name) = x \ "api_name"
      val JArray(List(JInt(page), JInt(number), JInt(completeTime), _)) = x \ "api_mission"
      val mission: Option[Mission] = if(page == BigInt(0) && number == BigInt(0)) {
        None
      } else {
        Some(Mission(page.toInt, number.toInt, completeTime.toLong))
      }
      val JArray(shipsRaw) = x \ "api_ship"
      val ships = shipsRaw.map { x =>
        val JInt(i) = x
        i.toInt
      }.filter(_ >= 0)
      DeckPort(id.toInt, name, mission, ships)
    }
  }
}
