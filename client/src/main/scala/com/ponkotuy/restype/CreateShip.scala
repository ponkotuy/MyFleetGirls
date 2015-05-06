package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query

import scala.collection.mutable
import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object CreateShip extends ResType {
  import ResType._

  // KDock + CreateShipのデータが欲しいのでKDockIDをKeyにCreateShipを溜めておく
  private[restype] val createShips: mutable.Map[Int, data.CreateShip] = mutable.Map()

  override def regexp: Regex = s"\\A$ReqKousyou/createship\\z".r

  override def postables(q: Query): Seq[Result] = {
    val createShip = data.CreateShip.fromMap(q.req, DeckPort.firstFleet.head)
    createShips(createShip.kDock) = createShip
    Nil
  }
}
