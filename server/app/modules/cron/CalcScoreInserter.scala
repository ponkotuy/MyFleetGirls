package modules.cron

import com.github.nscala_time.time.StaticDateTime
import models.db.{Admiral, CalcScore}
import tool.BattleScore
import util.{Cron, CronExecutor, Ymdh}

object CalcScoreInserter extends CronExecutor {
  override def exec(cron: Cron): Unit = {
    val now = StaticDateTime.now()
    val scores = Admiral.findAllIds().map { memberId =>
      val score = BattleScore.calcFromMemberId(memberId)
      val yyyymmddhh = Ymdh(now.getYear, now.getMonthOfYear, cron.day, cron.hour).toInt
      score.toCalcScore(memberId, yyyymmddhh, now.getMillis)
    }
    CalcScore.batchInsert(scores)
  }
}
