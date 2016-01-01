package ranking

import controllers.routes
import models.db.{Ranking => dbRanking, Admiral, CalcScore}
import ranking.common.{Ranking, RankingElement}
import scalikejdbc._
import com.github.nscala_time.time.Imports._
import util.Ymdh

object ScoreRanking extends Ranking {
  import Ranking._
  import DateTime._

  override val title: String = "当月戦果"
  override val comment: Seq[String] = Nil
  override val divClass: String = colmd3

  val cs = CalcScore.cs
  val r = dbRanking.r

  override def rankingQuery(limit: Int): Seq[RankingElement] = {
    val calc = scoresFromCalc()
    val observed = scoresFromObserved()
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

  private def scoresFromCalc(): Map[Long, Int] = {
    val scores = CalcScore.findAllBy(sqls.gt(cs.yyyymmddhh, Ymdh.monthHead(now())))
    scores.groupBy(_.memberId).mapValues(_.map(_.sum).max)
  }

  private def scoresFromObserved(): Map[Long, Int] = {
    val scores = dbRanking.findAllBy(sqls.gt(r.created, monthHead(now()).getMillis))
    scores.groupBy(_.memberId).mapValues(_.map(_.rate).max)
  }

  private[ranking] def monthHead(date: DateTime): DateTime = date.withDayOfMonth(1).withTime(0, 0, 0, 0)
}
