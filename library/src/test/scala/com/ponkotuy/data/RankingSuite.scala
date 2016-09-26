package com.ponkotuy.data

import org.json4s.native.JsonMethods._
import org.scalatest.FunSuite

/**
 *
 * @author ponkotuy
 * Date: 15/04/06.
 */
class RankingSuite extends FunSuite {
  test("success pattern") {
    val stream = getClass.getResource("/ranking.json").openStream()
    val json = try {
      parse(stream)
    } finally {
      stream.close()
    }
    val result = Ranking.fromJson(json \ "api_data")
    assert(result.nonEmpty)
    val ranking = result.head
    assert(ranking.no === 1)
    assert(ranking.rate === 24577)
  }
}
