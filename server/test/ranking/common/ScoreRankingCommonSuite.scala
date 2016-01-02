package ranking.common

import com.github.nscala_time.time.Imports._
import org.scalatest.FunSuite

class ScoreRankingCommonSuite extends FunSuite {
  import ranking.ScoreRankingCommon._

  test("monthHead") {
    assert(monthHead(new DateTime(2016, 1, 3, 10, 5)) === new DateTime(2016, 1, 1, 0, 0, 0, 0))
  }

  test("monthLast") {
    assert(monthLast(new DateTime(2016, 1, 3, 10, 5)) === new DateTime(2016, 1, 31, 0, 0, 0, 0))
    assert(monthLast(new DateTime(2016, 1, 31, 23, 59)) === new DateTime(2016, 1, 31, 0, 0, 0, 0))
    assert(monthLast(new DateTime(2016, 2, 12, 22, 1)) === new DateTime(2016, 2, 29, 0, 0, 0, 0))
  }
}
