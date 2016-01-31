package util

import com.github.nscala_time.time.Imports._
import org.scalatest.FunSuite

class MFGDateUtilSuite extends FunSuite {
  import MFGDateUtil._

  test("monthHead") {
    assert(monthHead(new DateTime(2016, 1, 3, 10, 5)) === new DateTime(2016, 1, 1, 0, 0, 0, 0))
    assert(monthHead(new DateTime(2016, 2, 1, 0, 0, Tokyo)) === new DateTime(2016, 2, 1, 0, 0, 0, 0, Tokyo))
    assert(monthHead(new DateTime(2016, 1, 31, 23, 59, UTC)) === new DateTime(2016, 1, 1, 0, 0, 0, 0, UTC))
  }

  test("monthLast") {
    assert(monthLast(new DateTime(2016, 1, 3, 10, 5)) === new DateTime(2016, 1, 31, 0, 0, 0, 0))
    assert(monthLast(new DateTime(2016, 1, 31, 23, 59)) === new DateTime(2016, 1, 31, 0, 0, 0, 0))
    assert(monthLast(new DateTime(2016, 2, 12, 22, 1)) === new DateTime(2016, 2, 29, 0, 0, 0, 0))
    assert(monthLast(new DateTime(2017, 2, 15, 22, 1)) === new DateTime(2017, 2, 28, 0, 0, 0, 0))
  }

  test("yearHead") {
    assert(yearHead(new DateTime(2016, 12, 31, 23, 59)) === new DateTime(2016, 1, 1, 0, 0, 0, 0))
  }
}

