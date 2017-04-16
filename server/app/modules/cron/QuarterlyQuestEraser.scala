package modules.cron

import models.db.Quest
import play.api.Logger
import util.{Cron, CronExecutor}
import scalikejdbc._

object QuarterlyQuestEraser extends CronExecutor {
  override def exec(cron: Cron): Unit = {
    Logger.info("Delete Monthly Quest")
    Quest.deleteAllBy(sqls"typ = 5")
  }
}
