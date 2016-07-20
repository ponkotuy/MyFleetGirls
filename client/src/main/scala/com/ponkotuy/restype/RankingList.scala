package com.ponkotuy.restype

import com.ponkotuy.data.Ranking
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.collection.breakOut
import scala.util.matching.Regex

case object RankingList extends ResType {
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
      Basic.getNickname.flatMap { nickname =>
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

/**
  * values = xs.map(_ * C) (1 <= C <= 9) のときのCを推定する
  */
class SecretConstCalculator(values: Set[Int] = Set.empty) {
  def add(others: Set[Int]) = new SecretConstCalculator(values ++ others)
  lazy val calc: Option[Int] = {
    if(values.size < 2) None
    else {
      Range(9, 0, -1).find { i =>
        values.forall(_ % i == 0)
      }
    }
  }
}
