package modules.cron

import models.db.{Admiral, ShipHistory}
import util.{Cron, CronExecutor}
import scalikejdbc._

import scala.concurrent.duration._

object ShipHistoryCutter extends CronExecutor {
  override def exec(cron: Cron): Unit = run()

  def run(): Unit = {
    val time = System.currentTimeMillis() - 365.days.toMillis
    Admiral.findAll().foreach { user =>
      import ShipHistory.{column => sh}
      ShipHistory.destroyBy(sqls.eq(sh.memberId, user.id).and.lt(sh.created, time))
      Thread.sleep(100L)
    }
  }
}
