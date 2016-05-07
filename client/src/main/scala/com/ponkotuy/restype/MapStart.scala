package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization._

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object MapStart extends ResType {
  import ResType._

  private[restype] var mapNext: Option[data.MapStart] = None
  private[restype] var startFleet: Int = 0
  private[restype] def areaId: Option[Int] = mapNext.map(_.mapAreaId)
  private[restype] def infoNo: Option[Int] = mapNext.map(_.mapInfoNo)

  override def regexp: Regex = s"\\A$ReqMap/start\\z".r

  override def postables(q: Query): Seq[Result] = {
    val next = data.MapStart.fromJson(q.obj)
    mapNext = Some(next)
    startFleet = q.req("api_deck_id").toInt
    NormalPostable("/map_start", write(mapNext), 1, next.summary) :: Nil
  }
}
