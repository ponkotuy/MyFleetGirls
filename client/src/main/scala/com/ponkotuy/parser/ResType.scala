package com.ponkotuy.parser

import com.github.theon.uri.Uri
import com.ponkotuy.util.Log
import scala.util.matching.Regex

/**
 *
 * @author ponkotuy
 * Date: 14/02/19.
 */
sealed abstract class ResType(val regexp: Regex)

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
  val ReqQuest = s"$Api/api_req_quest"
  val ReqNyukyo = s"$Api/api_req_nyukyo"

  case object LoginCheck extends ResType(s"$AuthMember/logincheck".r) // 取るべきではない
  case object Material extends ResType(s"$GetMember/material".r)
  case object Basic extends ResType(s"$GetMember/basic".r)
  case object Record extends ResType(s"$GetMember/record".r) // Basicの綺麗版
  case object Ship2 extends ResType(s"$GetMember/ship2".r) // どうせship3に含まれているんだろう？
  case object Ship3 extends ResType(s"$GetMember/ship3".r)
  case object NDock extends ResType(s"$GetMember/ndock".r)
  case object KDock extends ResType(s"$GetMember/kdock".r)
  case object Deck extends ResType(s"$GetMember/deck".r) // DeckPortと何が違うのか分からなくて困っている
  case object DeckPort extends ResType(s"$GetMember/deck_port".r)
  case object Practice extends ResType(s"$GetMember/practice".r) // 演習相手。取るべきではない
  case object Book2 extends ResType(s"$GetMember/book2".r)
  case object CreateShip extends ResType(s"$ReqKousyou/createship".r)
  case object GetShip extends ResType(s"$ReqKousyou/getship".r) // IDとshipIDのみ
  case object CreateItem extends ResType(s"$ReqKousyou/createitem".r)
  case object Charge extends ResType(s"$ReqHokyu/charge".r) // 特に要らない
  case object HenseiChange extends ResType(s"$ReqHensei/change".r) // 特に要らない
  case object HenseiLock extends ResType(s"$ReqHensei/lock".r)
  case object MissionStart extends ResType(s"$ReqMission/start".r) // 特に要らない
  case object KaisouPowerup extends ResType(s"$ReqKaisou/powerup".r)
  case object PracticeBattle extends ResType(s"$ReqPractice/battle".r)
  case object PracticeBattleResult extends ResType(s"$ReqPractice/battle_result".r)
  case object GetOthersDeck extends ResType(s"$ReqMember/getothersdeck".r) // 演習相手。取るべきではない
  case object MapStart extends ResType(s"$ReqMap/start".r)
  case object MapNext extends ResType(s"$ReqMap/next".r)
  case object SortieBattle extends ResType(s"$ReqSortie/battle".r)
  case object SortieBattleResult extends ResType(s"$ReqSortie/battleresult".r)
  case object ClearItemGet extends ResType(s"$ReqQuest/clearitemget".r)
  case object NyukyoStart extends ResType(s"$ReqNyukyo/start".r)
  case object MasterShip extends ResType(s"$GetMaster/ship".r)
  case object MasterPractice extends ResType(s"$GetMaster/practice".r)
  case object MasterUseItem extends ResType(s"$GetMaster/useitem".r) // 高速修復材とかの説明
  case object MasterFurniture extends ResType(s"$GetMaster/furniture".r) // 家具の説明
  case object MasterSlotItem extends ResType(s"$GetMaster/slotitem".r)
  case object MasterMapArea extends ResType(s"$GetMaster/maparea".r) // 鎮守府海域・南西諸島海域など
  case object MasterSType extends ResType(s"$GetMaster/stype".r)
  case object MasterMission extends ResType(s"$GetMaster/mission".r)
  case object ShipSWF extends ResType("""/kcs/ships/[0-9]+\.swf""".r)

  val values = Set(
    LoginCheck, Material, Basic, Record, Ship2, Ship3, NDock, KDock,
    Deck, DeckPort, Practice, Book2, CreateShip, GetShip, CreateItem,
    Charge, HenseiChange, HenseiLock, MissionStart, KaisouPowerup, PracticeBattle, PracticeBattleResult, GetOthersDeck,
    MapStart, SortieBattle, SortieBattleResult, ClearItemGet, NyukyoStart,
    MasterShip, MasterPractice, MasterUseItem, MasterFurniture, MasterSlotItem, MasterMapArea, MasterSType, MasterMission,
    ShipSWF
  )

  def fromUri(uri: String): Option[ResType] = {
    val path = Uri.parseUri(uri).pathRaw
    println(path)
    values.find(_.regexp.findFirstIn(path).isDefined)
  }
}
