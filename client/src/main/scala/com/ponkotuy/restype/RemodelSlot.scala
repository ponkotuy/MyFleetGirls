package com.ponkotuy.restype

import com.ponkotuy.data.Remodel
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object RemodelSlot extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$ReqKousyou/remodel_slot\\z".r

  override def postables(q: Query): Seq[Result] = {
    for {
      first <- FleetsState.firstFleet
      firstShip <- first.firstShip
      secondShip = first.secondShip
      remodel <- Remodel.fromJson(q.obj, q.req, firstShip, secondShip)
    } yield {
      NormalPostable("/remodel", write(remodel))
    }
  }.toList
}
