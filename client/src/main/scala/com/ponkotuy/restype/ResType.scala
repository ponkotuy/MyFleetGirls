package com.ponkotuy.restype

import com.netaporter.uri.Uri
import com.ponkotuy.parser.Query
import com.ponkotuy.util.Log
import org.json4s._

import scala.util.matching.Regex

/**
 *
 * 2014-04-23にAPI変更で知れず廃止されたAPIもあるものと思われる
 *
 * @author ponkotuy
 * Date: 14/02/19.
 */
abstract class ResType {
  def regexp: Regex
  def postables(q: Query): Seq[Result]
  implicit def formats: Formats = DefaultFormats
}

object ResType extends Log {
  type Req = Map[String, String]

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
  val ReqRanking = s"$Api/api_req_ranking"
  val ReqCombined = s"$Api/api_req_combined_battle"

  /*
  case object Record extends ResType(s"\\A$GetMember/record\\z".r) // Basicの綺麗版
  case object UseItem extends ResType(s"\\A$GetMember/useitem\\z".r) // 家具箱とか
  case object Practice extends ResType(s"\\A$GetMember/practice\\z".r) // 演習相手。取るべきではない
  case object MapCell extends ResType(s"\\A$GetMember/mapcell\\z".r) // Mapの各Cellで通ったことあるかどうか
  case object UpdateDeckName extends ResType(s"\\A$ReqMember/updatedeckname\\z".r) // DeckName変更
  case object Charge extends ResType(s"\\A$ReqHokyu/charge\\z".r)
  case object HenseiLock extends ResType(s"\\A$ReqHensei/lock\\z".r)
  case object MissionStart extends ResType(s"\\A$ReqMission/start\\z".r)
  case object KaisouPowerup extends ResType(s"\\A$ReqKaisou/powerup\\z".r)
  case object Remodeling extends ResType(s"\\A$ReqKaisou/remodeling\\z".r)
  case object Marriage extends ResType(s"\\A$ReqKaisou/marriage\\z".r) // ケッコンカッコカリ（はぁと
  case object KaisouLock extends ResType(s"\\A$ReqKaisou/lock\\z".r) // 装備ロック
  case object PracticeBattle extends ResType(s"\\A$ReqPractice/battle\\z".r)
  case object PracticeMidnightBattle extends ResType(s"\\A$ReqPractice/midnight_battle\\z".r)
  case object PracticeBattleResult extends ResType(s"\\A$ReqPractice/battle_result\\z".r)
  case object GetOthersDeck extends ResType(s"\\A$ReqMember/getothersdeck\\z".r) // 演習相手。取るべきではない
  case object SortieBattle extends ResType(s"\\A$ReqSortie/battle\\z".r)
  case object ClearItemGet extends ResType(s"\\A$ReqQuest/clearitemget\\z".r)
  case object NyukyoStart extends ResType(s"\\A$ReqNyukyo/start\\z".r)
  case object CombinedBattleResult extends ResType(s"\\A$ReqCombined/battleresult\\z".r)
  case object MasterPractice extends ResType(s"\\A$GetMaster/practice\\z".r)
  case object MasterUseItem extends ResType(s"\\A$GetMaster/useitem\\z".r) // 高速修復材とかの説明
  case object MasterFurniture extends ResType(s"\\A$GetMaster/furniture\\z".r) // 家具の説明
  case object MasterMapArea extends ResType(s"\\A$GetMaster/maparea\\z".r) // 鎮守府海域・南西諸島海域など
*/

  val values: Set[ResType] = Set(
    ApiStart2,
    Basic,
    LoginCheck,
    Port,
    Material,
    DeckPort,
    Deck,
    HenseiChange,
    PictureBook,
    NDock,
    KDock,
    CreateShip,
    GetShip,
    CreateItem,
    MapInfo,
    QuestList,
    RemodelSlot,
    RemodelSlotlist,
    RemodelSlotlistDetail,
    Ship2,
    Ship3,
    ShipSWF,
    SoundMP3,
    SlotItem,
    MapStart,
    MapNext,
    SortieBattleResult,
    CombinedBattleResult
  )

  def fromUri(uri: String): Option[ResType] = {
    val path = Uri.parse(uri).pathRaw
    println(path)
    values.find(_.regexp.findFirstIn(path).isDefined)
  }
}
