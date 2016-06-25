package modules.cron

import models.db.{AGOProgress, Quest}
import play.api.Logger
import scalikejdbc._
import util.{Cron, CronExecutor}

object WeeklyQuestEraser extends CronExecutor {
  override def exec(cron: Cron): Unit = {
    Logger.info("Delete Weekly Quest")
    Quest.deleteAllBy(sqls"typ = 2")
    Logger.info("Delete AGOProgress")
    AGOProgress.deleteAll()
  }
}
