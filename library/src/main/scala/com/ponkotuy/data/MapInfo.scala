package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/03/24.
 */
case class MapInfo(id: Int, cleared: Boolean, exbossFlag: Boolean, defeatedCount: Option[Int], eventMap: Option[EventMap])

object MapInfo {
  implicit val formats = DefaultFormats

  def fromJson(obj: JValue): List[MapInfo] =
    obj.extractOpt[List[RawMapInfo]].getOrElse(Nil).map(_.build)
}

case class RawMapInfo(
    api_id: Int,
    api_cleared: Int,
    api_exboss_flag: Int,
    api_defeat_count: Option[Int],
    api_eventmap: Option[RawEventMap]) {
  def build: MapInfo =
    MapInfo(api_id, api_cleared != 0, api_exboss_flag != 0, api_defeat_count, api_eventmap.map(_.build))
}

case class EventMap(hp: Option[Hp], state: Int, rank: Option[MapRank])

case class Hp(now: Int, max: Int)

object Hp {
  def fromRaw(raw: RawEventMap): Option[Hp] = {
    for {
      now <- raw.api_now_maphp
      max <- raw.api_max_maphp
    } yield Hp(now, max)
  }
}

case class RawEventMap(api_now_maphp: Option[Int], api_max_maphp: Option[Int], api_state: Int, api_selected_rank: Option[Int]) {
  def build: EventMap = {
    val hp = Hp.fromRaw(this)
    val rank = api_selected_rank.flatMap(MapRank.fromInt)
    EventMap(hp, api_state, rank)
  }
}

sealed abstract class MapRank(val v: Int, val str: String)

object MapRank {
  object Hei extends MapRank(1, "hei")
  object Otsu extends MapRank(2, "otsu")
  object Ko extends MapRank(3, "ko")

  val values = Vector(Hei, Otsu, Ko)
  def fromInt(v: Int): Option[MapRank] = values.find(_.v == v)
  def fromString(str: String): Set[MapRank] =
    values.filter { rank => str.contains(rank.str) }.toSet
}
