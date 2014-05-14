
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import play.api._
import play.libs.Akka
import akka.actor.Props
import scalikejdbc.SQLInterpolation._
import org.joda.time.DateTimeConstants
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

  private def sample(cron: Cron): Unit = {
    Logger.info(s"Sample EXEC: ${cron.hour}:${cron.minutes}")
  }
}
