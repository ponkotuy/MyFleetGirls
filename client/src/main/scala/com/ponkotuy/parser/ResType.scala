package com.ponkotuy.parser

import com.github.theon.uri.Uri
import com.ponkotuy.util.Log

/**
 *
 * @author ponkotuy
 * Date: 14/02/19.
 */
sealed abstract class ResType(val path: String)

object ResType extends Log {
  val Api = "/kcsapi"
  val AuthMember = s"$Api/api_auth_member"
  val GetMaster = s"$Api/api_get_master"
  val GetMember = s"$Api/api_get_member"
  val ReqKousyou = s"$Api/api_req_kousyou"
  val ReqHokyu = s"$Api/api_req_hokyu"
  val ReqHensei = s"$Api/api_req_hensei"
  val ReqMission = s"$Api/api_req_mission"
  val ReqKaisou = s"$Api/api_req_kaisou"
  val ReqPractice = s"$Api/api_req_practice"
  val ReqMember = s"$Api/api_req_member"
  val ReqMap = s"$Api/api_req_map"
  val ReqSortie = s"$Api/api_req_sortie"
  val ReqQuest

  case object LoginCheck extends ResType(s"$AuthMember/logincheck") // 取るべきではない
  case object Material extends ResType(s"$GetMember/material")
  case object Basic extends ResType(s"$GetMember/basic")
  case object Record extends ResType(s"$GetMember/record") // Basicの綺麗版
  case object Ship2 extends ResType(s"$GetMember/ship2") // どうせship3に含まれているんだろう？
  case object Ship3 extends ResType(s"$GetMember/ship3")
  case object NDock extends ResType(s"$GetMember/ndock")
  case object KDock extends ResType(s"$GetMember/kdock")
  case object Deck extends ResType(s"$GetMember/deck") // DeckPortと何が違うのこあ分からなくて困っている
  case object DeckPort extends ResType(s"$GetMember/deck_port")
  case object CreateShip extends ResType(s"$ReqKousyou/createship")
  case object GetShip extends ResType(s"$ReqKousyou/getship") // IDとshipIDのみ
  case object CreateItem extends ResType(s"$ReqKousyou/createitem")
  case object Charge extends ResType(s"$ReqHokyu/charge") // 特に要らない
  case object HenseiChange extends ResType(s"$ReqHensei/change") // 特に要らない
  case object MissionStart extends ResType(s"$ReqMission/start") // 特に要らない
  case object KaisouPowerup extends ResType(s"$ReqKaisou/powerup")
  case object PracticeBattle extends ResType(s"$ReqPractice/battle")
  case object PracticeBattleResult extends ResType(s"$ReqPractice/battle_result")
  case object GetOthersDeck extends ResType(s"$ReqMember/getothersdeck") // 演習相手。取るべきではない
  case object MapStart extends ResType(s"$ReqMap/start")
  case object MapNext extends ResType(s"$ReqMap/next")
  case object SortieBattle extends ResType(s"$ReqSortie/battle")
  case object SortieBattleResult extends ResType(s"$ReqSortie/battleresult")
  case object ClearItemGet extends ResType(s"$ReqQuest/clearitemget")
  case object MasterShip extends ResType(s"$GetMaster/ship")
  case object MasterPractice extends ResType(s"$GetMaster/mission")

  val values = Set(
    LoginCheck, Material, Basic, Record, Ship2, Ship3, NDock, KDock,
    Deck, DeckPort, CreateShip, GetShip, CreateItem,
    Charge, HenseiChange, MissionStart, KaisouPowerup, PracticeBattle, PracticeBattleResult, GetOthersDeck,
    MapStart, SortieBattle,
    MasterShip, MasterPractice
  )

  def fromUri(uri: String): Option[ResType] = {
    val path = Uri.parseUri(uri).pathRaw
    println(path)
    values.find(_.path == path)
  }
}
