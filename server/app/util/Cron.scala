package util

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.Actor
import com.github.nscala_time.time.Imports._

/**
 *
 * @author ponkotuy
 * Date: 14/05/12.
 */
case class Cron(minutes: Int, hour: Int, day: Int, month: Int, dayOfWeek: Int) {
  import Cron._
  def in(x: Cron): Boolean = {
    (x.minutes == aster || minutes == x.minutes) &&
      (x.hour == aster || hour == x.hour) &&
      (x.day == aster || day == x.day) &&
      (x.month == aster || month == x.month) &&
      (x.dayOfWeek == aster || dayOfWeek == x.dayOfWeek)
  }
}

object Cron {
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
}

case class CronSchedule(cron: Cron, f: Cron => Unit) {
  def exec(now: Cron): Unit = {
    if(now.in(cron)) f(now)
  }
}

/** 設定されたCronを実行するScheduler
  *
  * 重複実行は許容されるが、取り零しが発生する可能性があるので、1分より短かめの間隔で"minutes"を送信する必要がある
  */
class CronScheduler extends Actor {
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
    case x => println("Not Received: " + x)
  }
}
