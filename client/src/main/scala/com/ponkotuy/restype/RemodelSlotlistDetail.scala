package com.ponkotuy.restype

import com.ponkotuy.data.master.MasterRemodel
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object RemodelSlotlistDetail extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$ReqKousyou/remodel_slotlist_detail\\z".r

  override def postables(q: Query): Seq[Result] = {
    val opt = for {
      first <- FleetsState.firstFleet
      secondShip <- first.secondShip
      remodel <- MasterRemodel.fromJson(q.obj, q.req, secondShip)
    } yield {
      NormalPostable("/master_remodel", write(remodel))
    }
    opt.toList
  }
}
