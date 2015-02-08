
import akka.actor.Props
import com.github.nscala_time.time.StaticDateTimeZone
import models.db.{Admiral, Material, Quest}
import org.joda.time.{DateTimeConstants, LocalDate}
import play.api._
import play.api.mvc._
import play.libs.Akka
import scalikejdbc._
import util.{Cron, CronSchedule, CronScheduler}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 14/05/12.
 */
object Global extends WithFilters(Cors) with GlobalSettings{
  import util.Cron._

  override def onStart(app: Application): Unit = {
    val cron = Akka.system().actorOf(Props[CronScheduler], "cron")
    cron ! CronSchedule(Cron(0, 5, aster, aster, aster), _ => deleteDailyQuest())
    cron ! CronSchedule(Cron(0, 5, aster, aster, DateTimeConstants.MONDAY), _ => deleteWeeklyQuest())
    cron ! CronSchedule(Cron(0, 5, 1, aster, aster), _ => deleteMonthlyQuest())
    cron ! CronSchedule(Cron(17, 3, aster, aster, aster), _ => cutMaterialRecord())
    Akka.system().scheduler.schedule(0.seconds, 45.seconds, cron, "minutes")
  }

  private def deleteDailyQuest(): Unit = {
    Logger.info("Delete Daily Quest")
    Quest.deleteAllBy(sqls"typ in(2, 4, 5)")
  }

  private def deleteWeeklyQuest(): Unit = {
    Logger.info("Delete Weekly Quest")
    Quest.deleteAllBy(sqls"typ = 3")
  }

  private def deleteMonthlyQuest(): Unit = {
    Logger.info("Delete Monthly Quest")
    Quest.deleteAllBy(sqls"typ = 6")
  }

  private def cutMaterialRecord(): Unit = {
    Logger.info("Cut Material Record")
    Admiral.findAll().map { user =>
      val monthAgo = System.currentTimeMillis() - 30L*24*60*60*1000
      val materials = Material.findAllByUser(user.id, to = monthAgo)
      val days = materials.groupBy { mat =>
        new LocalDate(mat.created, StaticDateTimeZone.forOffsetHours(9))
      }.values
      days.map { daily =>
        val rest = Set(daily.minBy(_.steel).id, daily.maxBy(_.steel).id)
        daily.filterNot(mat => rest.contains(mat.id)).foreach(_.destroy())
      }
    }
  }
}

object Cors extends Filter {
  def apply(f: (RequestHeader) => Future[Result])(rh: RequestHeader): Future[Result] = {
    val result = f(rh)
    result.map { r =>
      r.withHeaders("Access-Control-Allow-Origin" -> "http://ponkotuy.github.io")
    }
  }
}
