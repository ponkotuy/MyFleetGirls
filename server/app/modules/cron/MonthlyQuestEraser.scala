package modules.cron

import models.db.Quest
import play.api.Logger
import scalikejdbc._
import util.{Cron, CronExecutor}

object MonthlyQuestEraser extends CronExecutor {
  override def exec(cron: Cron): Unit = {
    Logger.info("Delete Monthly Quest")
    Quest.deleteAllBy(sqls"typ = 3")
  }
}
