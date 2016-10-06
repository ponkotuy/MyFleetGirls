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

  def fromJson(obj: JValue): List[MapInfo] = {
    val res = (obj \ "api_map_info").extractOpt[List[RawMapInfo]].getOrElse(Nil).map(_.build)
    assert(res.nonEmpty, "Empty mapinfo")
    res
  }
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

case class EventMap(hp: Option[Hp], state: Int, rank: Option[Int])

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
    EventMap(hp, api_state, api_selected_rank)
  }
}
