package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import com.ponkotuy.restype.{DeckPort => DP}

import scala.util.Try
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
    if(change.id == 1) {
      if(change.shipId == -1)
        DP.firstFleet = DP.firstFleet.filterNot(_ == DP.firstFleet(change.shipIdx))
      else {
        DP.firstFleet = Try {
          // 入れ替えの場合、元いた奴を先に移しておく
          val sourceIdx = DP.firstFleet.indexOf(change.shipId)
          if(sourceIdx >= 0) DP.firstFleet = DP.firstFleet.updated(sourceIdx, DP.firstFleet(change.shipIdx))
          // update
          DP.firstFleet.updated(change.shipIdx, change.shipId)
        }.getOrElse {
          // 例外が来たら追記
          DP.firstFleet = DP.firstFleet.filterNot(_ == change.shipId)
          DP.firstFleet :+ change.shipId
        }
      }
    }
    Nil
  }
}
