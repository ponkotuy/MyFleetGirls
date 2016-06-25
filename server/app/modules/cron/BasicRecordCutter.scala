package modules.cron

import models.db.{Admiral, Basic}
import org.joda.time.LocalDate
import play.api.Logger
import util.{Cron, CronExecutor}

import scala.concurrent.duration._

object BasicRecordCutter extends CronExecutor {
  import util.MFGDateUtil._

  override def exec(cron: Cron): Unit = {
    Logger.info("Cut Basic Record")
    Admiral.findAll().foreach { user =>
      val monthAgo = System.currentTimeMillis() - 30.days.toMillis
      val basics = Basic.findAllByUser(user.id, to = monthAgo)
      val days = basics.groupBy { b =>
        new LocalDate(b.created, Tokyo)
      }.values
      days.foreach { daily =>
        val rest = Set(daily.minBy(_.experience).id, daily.maxBy(_.experience).id)
        daily.filterNot(b => rest.contains(b.id)).foreach(_.destroy())
      }
    }
  }
}
