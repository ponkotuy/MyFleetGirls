package models.req

import models.db.CalcScore

case class ScoreDays(date: String, score: Diff, monthlyExp: Diff, yearlyExp: Int, eo: Diff, lastEo: Int, quest: Diff)

object ScoreDays {
  import Diff._
  def fromScores(now: CalcScore, prev: CalcScore): ScoreDays =
    ScoreDays(now.prettyDate,
      diff(now.sum, prev.sum),
      diff(now.monthlyExp, prev.monthlyExp),
      now.yearlyExp,
      diff(now.eo, prev.eo),
      now.lastEo,
      diff(now.quest, prev.quest)
    )
}
