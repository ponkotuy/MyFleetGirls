package modules.cron

import models.db.Quest
import play.api.Logger
import scalikejdbc._
import util.{Cron, CronExecutor}

object DailyQuestEraser extends CronExecutor {
  override def exec(cron: Cron): Unit = {
    Logger.info("Delete Daily Quest")
    Quest.deleteAllBy(sqls"typ = 1")
  }
}
