package util

import com.github.nscala_time.time.Imports._
import org.scalatest.FunSuite

class YmdhSuite extends FunSuite {
  import Ymdh._
  test("monthHead") {
    assert(monthHead(new DateTime(2016, 1, 3, 10, 5)) === Ymdh(2016, 1, 1, 0))
  }
}
