package util

import com.github.nscala_time.time.Imports._

case class Ymdh(year: Int, month: Int, day: Int, hour: Int) extends Ordered[Ymdh] {
  import Ymdh._

  def toInt = year * 1000000 + month * 10000 + day * 100 + hour
  def toJp = s"${year}年${month}月${day}日${hour}時"
  def toEn = s"${year}/${month}/${day}:${hour}"

  def toDateTime = new DateTime(year, month, day, hour, 0)

  def next(period: Period) = fromDateTime(toDateTime + period)
  def +(period: Period) = next(period)
  def nextHour = next(1.hour)
  def nextDay = next(1.day)
  def nextMonth = next(1.month)
  def nextYear = next(1.year)

  def last(period: Period) = fromDateTime(toDateTime - period)
  def -(period: Period) = last(period)
  def lastHour = last(1.hour)
  def lastDay = last(1.day)
  def lastMonth = last(1.month)
  def lastYear = last(1.year)

  override def compare(that: Ymdh): Int = ordering.compare(this, that)
}

object Ymdh {
  def fromDateTime(dt: DateTime): Ymdh =
    Ymdh(dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth, dt.getHourOfDay)

  def fromInt(i: Int): Ymdh =
    new Ymdh(i / 1000000, i % 1000000 / 10000, i % 10000 / 100, i % 100)

  def monthHead(date: DateTime): Ymdh = Ymdh(date.getYear, date.getMonthOfYear, 1, 0)

  def now(zone: DateTimeZone): Ymdh = fromDateTime(DateTime.now(zone))

  val ordering: Ordering[Ymdh] = Ordering.by(unapply)
}
