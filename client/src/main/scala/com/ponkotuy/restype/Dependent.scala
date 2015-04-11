package com.ponkotuy.restype

import com.ponkotuy.data

import scala.collection.mutable

/**
 * Date: 15/04/12.
 */
private[restype] object Dependent {
  // 現在進行中のStage情報がBattleResultで必要なので置いておく
  var mapNext: Option[data.MapStart] = None
  // 出撃中の艦隊番号
  var startFleet: Int = 0
  // 艦隊情報がRoute等で必要なので溜めておく
  var fleets: Seq[List[Int]] = Nil
  var firstFleet: List[Int] = Nil
  // KDock + CreateShipのデータが欲しいのでKDockIDをKeyにCreateShipを溜めておく
  val createShips: mutable.Map[Int, data.CreateShip] = mutable.Map()
}
