package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object SortieBattleResult extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$ReqSortie/battleresult\\z".r

  override def postables(q: Query): Seq[Result] = {
    val result = data.BattleResult.fromJson(q.obj)
    NormalPostable("/battle_result", write((result, MapStart.mapNext)), 1, result.summary) :: Nil
  }
}

case object CombinedBattleResult extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$ReqCombined/battleresult\\z".r

  override def postables(q: Query): Seq[Result] = SortieBattleResult.postables(q)
}
