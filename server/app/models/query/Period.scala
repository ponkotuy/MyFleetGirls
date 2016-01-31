package models.query

import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import scalikejdbc._

import scala.util.Try

/**
 * Date: 14/11/27.
 */
case class Period(from: Option[LocalDate], to: Option[LocalDate], default: Boolean) {
  import Period._
  import util.MFGDateUtil._

  def where(target: SQLSyntax): SQLSyntax = whereOpt(target).getOrElse(sqls"true")
  def whereOpt(target: SQLSyntax): Option[SQLSyntax] = {
    val x = from.map { it => sqls.gt(target, it.toDate.getTime) }
    val y = to.map { it => sqls.lt(target, it.toDate.getTime) }
    sqls.toAndConditionOpt(x, y)
  }

  def fromStr = from.getOrElse(DefaultStart).toString(ISODateTimeFormat.date())
  def toStr = to.getOrElse(LocalDate.now(Tokyo)).toString(ISODateTimeFormat.date())
}

object Period {
  lazy val DefaultStart = new LocalDate(2014, 1, 1)

  def fromStr(from: String, to: String): Period = {
    val fromDate = Try { LocalDate.parse(from, ISODateTimeFormat.date()) }
    val toDate = Try { LocalDate.parse(to, ISODateTimeFormat.date()) }
    Period(fromDate.toOption, toDate.toOption, fromDate.isFailure && toDate.isFailure)
  }
}
