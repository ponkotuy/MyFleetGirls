package modules.cron

import models.db.{Admiral, Material}
import org.joda.time.LocalDate
import play.api.Logger
import util.{Cron, CronExecutor}

import scala.concurrent.duration._

object MaterialRecordCutter extends CronExecutor {
  import util.MFGDateUtil._

  override def exec(cron: Cron): Unit = {
    Logger.info("Cut Material Record")
    Admiral.findAll().foreach { user =>
      val monthAgo = System.currentTimeMillis() - 30.days.toMillis
      val materials = Material.findAllByUser(user.id, to = monthAgo)
      val days = materials.groupBy { mat =>
        new LocalDate(mat.created, Tokyo)
      }.values
      days.foreach { daily =>
        val rest = Set(daily.minBy(_.steel).id, daily.maxBy(_.steel).id)
        daily.filterNot(mat => rest.contains(mat.id)).foreach(_.destroy())
      }
    }
  }
}
