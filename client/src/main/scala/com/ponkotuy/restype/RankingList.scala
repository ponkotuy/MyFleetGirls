package com.ponkotuy.restype

import com.ponkotuy.data.Ranking
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.collection.breakOut
import scala.util.matching.Regex

class RankingList(admiral: Admiral) extends ResType {
  import ResType._

  private[this] var calculator = new SecretConstCalculator()

  // 送信に必要な定数が計算できなかったとき、計算できるようになったら送信する為に保持するstate
  var myRanking: Option[Ranking] = None

  override def regexp: Regex = s"\\A$ReqRanking/getlist\\z".r

  override def postables(q: Query): Seq[Result] = {
    val rankings = Ranking.fromJson(q.obj)
    val ratesWithConst: Set[Int] = rankings.map { r => r.rate / r.no }(breakOut)
    calculator = calculator.add(ratesWithConst)
    myRanking = myRanking.orElse {
      admiral.nickname.flatMap { nickname =>
        rankings.find(_.nickname == nickname)
      }
    }
    val ranking = myRanking.flatMap { rank =>
      if(rank.rate == 0) Some(rank)
      else calculator.calc.map { const => rank.copy(rate = rank.rate / const / rank.no) }
    }
    if(ranking.isDefined) myRanking = None
    ranking.map { r => NormalPostable("/ranking", write(r), ver = 2, message = s"順位 -> ${r.no}, 戦果 -> ${r.rate}") }.toSeq
  }
}
