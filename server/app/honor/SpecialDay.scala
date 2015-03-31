package honor

import com.github.nscala_time.time.Imports._

/**
 *
 * @author ponkotuy
 * Date: 15/04/01.
 */
object SpecialDay extends HonorCategory {
  override def category: Int = 15

  override def approved(memberId: Long): List[String] = {
    val now = DateTime.now(DateTimeZone.forOffsetHours(9))
    val monthDay = MonthDay.fromJoda(now)
    Honors.get(monthDay).toList
  }

  val Honors = Map(MonthDay(4, 1) -> "エイプリルフール")
}

case class MonthDay(month: Int, day: Int)

object MonthDay {
  def fromJoda(date: DateTime): MonthDay = new MonthDay(date.getMonthOfYear, date.getDayOfMonth)
}
