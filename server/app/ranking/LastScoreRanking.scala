package ranking

import controllers.routes
import models.db.Admiral
import ranking.ScoreRankingCommon._
import ranking.common.{RankingElement, Ranking}
import com.github.nscala_time.time.Imports._

/**
  * Date: 2016/01/02
  * @author ponkotuy
  */
object LastScoreRanking extends Ranking {
  import Ranking._
  import util.MFGDateUtil._

  override val title: String = "先月戦果"
  override val comment: Seq[String] = Nil
  override val divClass: String = colmd3

  override def rankingQuery(limit: Int): Seq[RankingElement] = {
    val interval = new Interval(monthHead(DateTime.now(Tokyo) - 1.month), monthLast(DateTime.now(Tokyo) - 1.month))
    val calc = scoresFromCalc(interval)
    val observed = scoresFromObserved(interval)
    val merged = (calc.keySet ++ observed.keySet).map { memberId =>
      memberId -> Seq(calc.get(memberId), observed.get(memberId)).flatten.max
    }.toSeq.sortBy(-_._2).take(limit + 5)
    merged.flatMap { case (memberId, score) =>
      Admiral.find(memberId).map { admiral =>
        val url = routes.UserView.user(memberId).toString
        RankingElement(admiral.nickname, <span>{score}</span>, url, score)
      }
    }.take(limit)
  }
}
