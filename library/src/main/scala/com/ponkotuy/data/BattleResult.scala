package com.ponkotuy.data

import org.json4s._
import scala.util.Try
import com.ponkotuy.tool.Pretty

/**
 *
 * @author ponkotuy
 * Date: 14/04/17.
 */
case class BattleResult(
    enemies: List[Int], winRank: String, exp: Int, mvp: Int,
    baseExp: Int, shipExp: List[Int], lostFlag: List[Boolean], questName: String, questLevel: Int, enemyDeck: String,
    firstClear: Boolean, getShip: Option[GetShip]) {
  def summary: String = Pretty(
    Map(
      ("Rank", winRank),
      ("MVP", mvp),
      ("Enemy", enemyDeck),
      ("ドロップ艦娘", getShip.map(_.name).getOrElse("なし"))
    )
  )
}

object BattleResult {
  implicit val format = DefaultFormats

  def fromJson(obj: JValue): BattleResult = {
    val enemies = toIntList(obj \ "api_ship_id").tail
    val JString(winRank) = obj \ "api_win_rank"
    val exp = (obj \ "api_get_exp").extract[Int]
    val mvp = (obj \ "api_mvp").extract[Int]
    val baseExp = (obj \ "api_get_base_exp").extract[Int]
    val shipExp = toIntList(obj \ "api_get_ship_exp").tail
    val lostFlag = toIntList(obj \ "api_lost_flag").tail.map(_ != 0)
    val JString(questName) = obj \ "api_quest_name"
    val questLevel = (obj \ "api_quest_level").extract[Int]
    val JString(enemyDeck) = obj \ "api_enemy_info" \ "api_deck_name"
    val firstClear = (obj \ "api_first_clear").extract[Int] != 0
    val getShip = GetShip.fromJson(obj \ "api_get_ship")
    BattleResult(
      enemies, winRank, exp, mvp, baseExp, shipExp, lostFlag, questName, questLevel, enemyDeck, firstClear, getShip
    )
  }

  def toIntList(obj: JValue): List[Int] = {
    val JArray(xs) = obj
    xs.map(_.extract[Int])
  }
}

case class GetShip(id: Int, stype: String, name: String)

object GetShip {
  implicit val format = DefaultFormats

  def fromJson(obj: JValue): Option[GetShip] = {
    Try {
      val id = (obj \ "api_ship_id").extract[Int]
      val JString(stype) = obj \ "api_ship_type"
      val JString(name) = obj \ "api_ship_name"
      GetShip(id, stype, name)
    }.toOption
  }

  val empty = GetShip(0, "-", "無し")
}
