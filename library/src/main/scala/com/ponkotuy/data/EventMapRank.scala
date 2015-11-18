package com.ponkotuy.data

/**
  * Created by yosuke on 11/19/15.
  */
case class EventMapRank(mapAreaId: Int, mapNo: Int, rank: Int)

object EventMapRank {
  def fromReq(req: Map[String, String]): Option[EventMapRank] = {
    for {
      mapAreaId <- req.get("api_maparea_id")
      mapNo <- req.get("api_map_no")
      rank <- req.get("api_rank")
    } yield new EventMapRank(mapAreaId.toInt, mapNo.toInt, rank.toInt)
  }
}
