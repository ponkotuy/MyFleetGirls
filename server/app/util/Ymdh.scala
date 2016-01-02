package util

import com.github.nscala_time.time.Imports._

case class Ymdh(year: Int, month: Int, day: Int, hour: Int) {
  def toInt = year * 1000000 + month * 10000 + day * 100 + hour
}

object Ymdh {
  def fromDate(dt: DateTime): Ymdh =
    Ymdh(dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth, dt.getHourOfDay)

  def fromInt(i: Int): Ymdh =
    new Ymdh(i / 1000000, i % 1000000 / 10000, i % 10000 / 100, i % 100)

  def monthHead(date: DateTime): Ymdh = Ymdh(date.getYear, date.getMonthOfYear, 1, 0)
}
