package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization._

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object RemodelSlotlist extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$ReqKousyou/remodel_slotlist\\z".r

  override def postables(q: Query): Seq[Result] = {
    val secondShip = FleetsState.firstFleet.flatMap(_.secondShip)
    val result = data.RemodelSlotlist.fromJson(q.obj, secondShip)
    NormalPostable("/remodel_slot", write(result)) :: Nil
  }
}
