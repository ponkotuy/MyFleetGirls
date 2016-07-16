package com.ponkotuy.restype

import com.ponkotuy.data.Ranking
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object RankingList extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$ReqRanking/getlist\\z".r

  override def postables(q: Query): Seq[Result] = {
    Basic.getNickname.map { nickname =>
      Ranking.fromJson(q.obj).filter(_.nickname == nickname).map { rank =>
        NormalPostable("/ranking", write(rank))
      }
    }.getOrElse(Nil)
  }
}
