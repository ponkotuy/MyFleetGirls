
import com.github.nscala_time.time.StaticDateTimeZone
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import play.api._
import play.libs.Akka
import akka.actor.Props
import scalikejdbc._
import org.joda.time.{LocalDate, DateTimeConstants}
import util.{Cron, CronSchedule, CronScheduler}
import models.Quest

/**
 *
 * @author ponkotuy
 * Date: 14/05/12.
 */
object Global extends GlobalSettings {
  import Cron._

  override def onStart(app: Application): Unit = {
    val cron = Akka.system().actorOf(Props[CronScheduler], "cron")
    cron ! CronSchedule(Cron(0, 5, aster, aster, aster), _ => deleteDailyQuest())
    cron ! CronSchedule(Cron(0, 5, aster, aster, DateTimeConstants.MONDAY), _ => deleteWeeklyQuest())
    cron ! CronSchedule(Cron(17, 3, aster, aster, aster), _ => cutMaterialRecord())
    // cron ! CronSchedule(Cron(aster, aster, aster, aster, aster), sample)
    Akka.system().scheduler.schedule(0.seconds, 45.seconds, cron, "minutes")
  }

  private def deleteDailyQuest(): Unit = {
    Logger.info("Delete Daily Quest")
    Quest.deleteAllBy(sqls"typ = 2")
  }

  private def deleteWeeklyQuest(): Unit = {
    Logger.info("Delete Weekly Quest")
    Quest.deleteAllBy(sqls"typ = 3")
  }

  private def cutMaterialRecord(): Unit = {
    Logger.info("Cut Material Record")
    models.Admiral.findAll().map { user =>
      val monthAgo = System.currentTimeMillis() - 30L*24*60*60*1000
      val materials = models.Material.findAllByUser(user.id, to = monthAgo)
      val days = materials.groupBy { mat =>
        new LocalDate(mat.created, StaticDateTimeZone.forOffsetHours(9))
      }.values
      days.map { daily =>
        val rest = Set(daily.minBy(_.steel).id, daily.maxBy(_.steel).id)
        daily.filterNot(mat => rest.contains(mat.id)).foreach(_.destroy())
      }
    }
  }

  private def sample(cron: Cron): Unit = {
    Logger.info(s"Sample EXEC: ${cron.hour}:${cron.minutes}")
  }
}
