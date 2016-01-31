package util

import com.github.nscala_time.time.Imports._

object MFGDateUtil {
  val Tokyo = DateTimeZone.forID("Asia/Tokyo")
  val UTC = DateTimeZone.forID("UTC")

  def monthHead(date: DateTime): DateTime = date.withDayOfMonth(1).withTime(0, 0, 0, 0)
  def monthLast(date: DateTime): DateTime =
    ((date + 1.month).withDayOfMonth(1) - 1.day).withTime(0, 0, 0, 0)
  def yearHead(now: DateTime): DateTime =
    new LocalDate(now.getYear, 1, 1).toDateTime(LocalTime.MIDNIGHT)
}
