package com.ponkotuy.restype

import com.ponkotuy.data.{CreateShipWithId, DeleteKDock}
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization._

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object GetShip extends ResType {
  import ResType._
  override def regexp: Regex = s"\\A$ReqKousyou/getship\\z".r

  override def postables(q: Query): Seq[Result] = {
    val kdock = DeleteKDock.fromReq(q.req, q.obj)
    NormalPostable("/delete_kdock", write(kdock)) ::
        CreateShip.createShips.remove(kdock.kDockId).map { cship =>
          val withId = CreateShipWithId(cship, kdock.shipId)
          NormalPostable("/createship", write(withId), 2, withId.summary)
        }.toList
  }
}
