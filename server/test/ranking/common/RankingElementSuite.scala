package ranking.common

import org.scalatest.FunSuite

class RankingElementSuite extends FunSuite {
  test("ranking ids are unique") {
    val ids = RankingType.values.flatMap { typ =>
      typ.rankings.map(_.id)
    }
    assert(ids.distinct.length === ids.length)
  }
}
