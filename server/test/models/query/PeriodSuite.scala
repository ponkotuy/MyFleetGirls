package models.query

import com.github.nscala_time.time.Imports._
import org.scalatest.FunSuite
import scalikejdbc._

class PeriodSuite extends FunSuite {
  // 2017/9/20 0:0:0 <= created < 2017/9/24 0:0:0になるようにする
  test("Period.where") {
    val now = new LocalDate(2017, 9, 20)
    val period = Period(Some(now), Some(now.plusDays(3)), default = true)
    val syntax = period.where(sqls"created")
    assert(syntax.value === " created >= ? and  created < ?")
    assert(syntax.parameters === Seq(1505833200000L, 1506178800000L))
  }
}
