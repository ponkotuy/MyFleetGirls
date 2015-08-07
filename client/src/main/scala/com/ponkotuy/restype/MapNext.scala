package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.data.MapRoute
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object MapNext extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$ReqMap/next\\z".r

  override def postables(q: Query): Seq[Result] = {
    val next = data.MapStart.fromJson(q.obj)
    val result = MapStart.mapNext.map { dep =>
      val route = MapRoute.fromMapNext(dep, next, DeckPort.fleets(MapStart.startFleet - 1))
      NormalPostable("/map_route", write(route), 1, route.summary)
    }
    MapStart.mapNext = Some(next)
    println(next.summary)
    result.toList
  }
}
