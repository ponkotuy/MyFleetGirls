package modules.cron

import com.github.nscala_time.time.StaticDateTime
import util.{Cron, CronExecutor}

object MonthlyCalcScoreInserter extends CronExecutor {
  override def exec(cron: Cron): Unit = {
    val year = StaticDateTime.now().getYear
    if(cron.isEndOfMonth(year)) CalcScoreInserter.exec(cron)
  }
}
