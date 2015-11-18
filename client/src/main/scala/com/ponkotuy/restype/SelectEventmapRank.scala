package com.ponkotuy.restype

import com.ponkotuy.data.EventMapRank
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
  *
  * @author ponkotuy
  * Date: 11/19/15.
  */
case object SelectEventmapRank extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$ReqMap/select_eventmap_rank\\z".r

  override def postables(q: Query): Seq[Result] = {
    EventMapRank.fromReq(q.req).map { mapRank =>
      NormalPostable("/event_map_rank", write(mapRank), ver = 2)
    }
  }.toList
}
