package util

import akka.actor.{Actor, ActorRef, ActorSystem, InvalidActorNameException, Props}
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

/**
 *
 * @author ponkotuy
 * Date: 14/05/12.
 */
case class Cron(minutes: Int, hour: Int, day: Int, month: Int, dayOfWeek: Int) {
  import Cron._
  import com.github.nscala_time.time.Imports._

  def in(x: Cron): Boolean = {
    (x.minutes == aster || minutes == x.minutes) &&
      (x.hour == aster || hour == x.hour) &&
      (x.day == aster || day == x.day) &&
      (x.month == aster || month == x.month) &&
      (x.dayOfWeek == aster || dayOfWeek == x.dayOfWeek)
  }

  def isEndOfMonth(year: Int): Boolean = {
    if(isAster(month) || isAster(day)) return false
    (new DateTime(year, month, day, 0, 0, 0) + 1.day).getMonthOfYear != month
  }
}

object Cron {
  import com.github.nscala_time.time.Imports._
  val aster = -1

  def fromLong(millis: Long = System.currentTimeMillis()): Cron = {
    val time = new DateTime(millis)
    fromDateTime(time)
  }

  def fromDateTime(time: DateTime): Cron = {
    Cron(
      time.minuteOfHour().get(),
      time.hourOfDay().get(),
      time.dayOfMonth().get(),
      time.monthOfYear().get(),
      time.dayOfWeek().get()
    )
  }

  def now: Cron = fromDateTime(DateTime.now)
  def isAster(v: Int): Boolean = v < 0
}

case class CronSchedule(cron: Cron, f: Cron => Unit) {
  def exec(now: Cron): Unit = {
    if(now.in(cron)) f(now)
  }
}

trait CronExecutor {
  def exec(cron: Cron): Unit
  def schedule(cron: Cron): CronSchedule = CronSchedule(cron, exec)
  def schedule(minutes: Int, hour: Int, day: Int, month: Int, dayOfWeek: Int): CronSchedule =
    schedule(Cron(minutes, hour, day, month, dayOfWeek))
}

/** 設定されたCronを実行するScheduler
  *
  * 重複実行は許容されるが、取り零しが発生する可能性があるので、1分より短かめの間隔で"minutes"を送信する必要がある
  */
class CronScheduler extends Actor {
  import context.dispatcher

  var schedules: List[CronSchedule] = Nil
  var lastExec: Long = 0 // Original Minutes (SystemTime / (60 * 1000))

  override def receive = {
    case s: CronSchedule => schedules = s :: schedules
    case "minutes" =>
      val now = System.currentTimeMillis()
      val minutes = now / (60 * 1000)
      if(lastExec < minutes) {
        val cron = Cron.fromLong(now)
        schedules.foreach { it =>
          Future { it.exec(cron) }
        }
        lastExec = minutes
      }
    case x => Logger.warn("Not Received: " + x)
  }
}

object CronScheduler {
  import scala.concurrent.duration._

  /**
    * Create CronScheduler Actor
    *
    * {{{
    * val system = ActorSystem()
    * val cron = CronScheduler.create(system, "name")
    * cron ! CronSchedule(Cron(0, 5, aster, aster, aster), func)
    * }}}
    *
    * @throws InvalidActorNameException : if you use invalid actor name.
    */
  def create(system: ActorSystem, name: String)(implicit ec: ExecutionContext): ActorRef = {
    val cron = system.actorOf(Props[CronScheduler], name)
    system.scheduler.schedule(45.seconds, 45.seconds, cron, "minutes")
    cron
  }
}
