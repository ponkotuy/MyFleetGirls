package ranking

import com.github.nscala_time.time.Imports._
import controllers.routes
import models.db.{Admiral, Ranking => dbRanking}
import org.json4s.JValue
import ranking.common.{Ranking, RankingData, RankingElement}
import ranking.data.Score

object ScoreRanking extends Ranking {
  import Ranking._
  import ScoreRankingCommon._
  import util.MFGDateUtil._

  override val id = 9
  override val title: String = "当月戦果"
  override val comment: Seq[String] = Nil
  override val divClass: String = colmd3

  override def rankingQuery(limit: Int): Seq[RankingElement] = {
    val interval = new Interval(monthHead(DateTime.now(Tokyo)), DateTime.now(Tokyo))
    val calc = scoresFromCalc(interval)
    val observed = scoresFromObserved(interval)
    val merged = (calc.keySet ++ observed.keySet).map { memberId =>
      memberId -> Seq(calc.get(memberId), observed.get(memberId)).flatten.max
    }.toSeq.sortBy(-_._2).take(limit + 5)
    merged.flatMap { case (memberId, score) =>
      Admiral.find(memberId).map { admiral =>
        val url = routes.UserView.user(memberId).toString
        RankingElement(admiral.id, admiral.nickname, Score(score), url, score)
      }
    }.take(limit)
  }

  // JSONになったRankingDataをdeserializeする
  override def decodeData(v: JValue): Option[RankingData] = Score.decode(v)
}
