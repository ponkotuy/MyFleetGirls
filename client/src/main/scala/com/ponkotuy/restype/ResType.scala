package com.ponkotuy.restype

import com.netaporter.uri.Uri
import com.ponkotuy.parser.Query
import com.ponkotuy.util.Log
import org.json4s._

import scala.util.matching.Regex

/**
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

  val values: Vector[ResType] = Vector(
    ApiStart2,
    Basic,
    LoginCheck,
    Port,
    Material,
    DeckPort,
    Deck,
    HenseiChange,
    PresetSelect,
    PictureBook,
    NDock,
    KDock,
    CreateShip,
    GetShip,
    CreateItem,
    MapInfo,
    SelectEventmapRank,
    QuestList,
    RemodelSlot,
    RemodelSlotlist,
    RemodelSlotlistDetail,
    Ship2,
    Ship3,
    ShipDeck,
    ShipSWF,
    SoundMP3,
    SlotItem,
    MapStart,
    MapNext,
    MapSWF,
    ObfuscatedMapSWF, // 判定順序の問題で必ずMapSWFの後でないといけない
    SortieBattleResult,
    CombinedBattleResult,
    RequireInfo
  )

  def fromUri(uri: String): Option[ResType] = {
    val path = Uri.parse(uri).pathRaw
    logger.info("Parse KC Request. path:{}", path)
    values.find(_.regexp.findFirstIn(path).isDefined)
  }
}
