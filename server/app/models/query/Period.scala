package models.query

import com.github.nscala_time.time.Imports._
import org.joda.time.format.ISODateTimeFormat
import scalikejdbc._
import util.MFGDateUtil

import scala.util.Try

case class Period(from: Option[LocalDate], to: Option[LocalDate], default: Boolean) {
  import Period._
  import util.MFGDateUtil._

  def where(target: SQLSyntax): SQLSyntax = whereOpt(target).getOrElse(sqls"true")
  def whereOpt(target: SQLSyntax): Option[SQLSyntax] = {
    val x = from.map { it => sqls.ge(target, it.toDateTimeAtStartOfDay(MFGDateUtil.Tokyo).getMillis) }
    val y = to.map { it => sqls.lt(target, it.toDateTimeAtStartOfDay(MFGDateUtil.Tokyo).plusDays(1).getMillis) }
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
