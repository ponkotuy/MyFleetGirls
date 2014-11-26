package models.query

import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import scalikejdbc._

import scala.util.Try

/**
 * Date: 14/11/27.
 */
case class Period(from: LocalDate, to: LocalDate, default: Boolean) {
  def where(target: SQLSyntax) = sqls"${from.toDate.getTime} < $target and $target < ${to.toDate.getTime}"
  def fromStr = from.toString(ISODateTimeFormat.date())
  def toStr = to.toString(ISODateTimeFormat.date())
}

object Period {
  lazy val DefaultStart = new LocalDate(2014, 1, 1)

  def fromStr(from: String, to: String): Period = {
    val fromDate = Try { LocalDate.parse(from, ISODateTimeFormat.date()) }
    val toDate = Try { LocalDate.parse(to, ISODateTimeFormat.date()) }
    Period(fromDate.getOrElse(DefaultStart), toDate.getOrElse(LocalDate.now()), fromDate.isFailure && toDate.isFailure)
  }
}
