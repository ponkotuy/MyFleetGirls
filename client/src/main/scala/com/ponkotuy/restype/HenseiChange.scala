package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query

import scala.util.matching.Regex

/**
 * 第一艦隊の情報のみ変更。めんどいので特にサーバは更新しない
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object HenseiChange extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$ReqHensei/change\\z".r

  override def postables(q: Query): Seq[Result] = {
    val change = data.HenseiChange.fromMap(q.req)
    if (change.shipId == -1) // 純粋な削除
      FleetsState.getFleet(change.id).foreach(_.removeShip(change.shipIdx))
    else {
      // 入れ替えの場合、元いた奴を先に移しておく
      FleetsState.getFleet(change.id).foreach { updatedFleet =>
        for {
          sourceIdx <- FleetsState.findShipIdx(change.shipId)
          sourceFleet <- FleetsState.getFleet(sourceIdx._1)
        } {
          updatedFleet.getShip(change.shipIdx) match {
            case Some(updatedShipId) => sourceFleet.updateShip(sourceIdx._2, updatedShipId)
            case None => sourceFleet.removeShip(sourceIdx._2)
          }
        }
        if(!updatedFleet.updateShip(change.shipIdx, change.shipId))
          updatedFleet.addShip(change.shipId)
      }
    }
    Nil
  }
}
