package ranking.common

import com.github.nscala_time.time.Imports._
import org.scalatest.FunSuite

class ScoreRankingSuite extends FunSuite {
  import ranking.ScoreRanking._
  test("monthHead") {
    assert(monthHead(new DateTime(2016, 1, 3, 10, 5)) === new DateTime(2016, 1, 1, 0, 0, 0, 0))
  }
}
