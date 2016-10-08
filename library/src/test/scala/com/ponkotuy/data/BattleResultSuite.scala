package com.ponkotuy.data

import org.json4s.native.JsonMethods._
import org.scalatest.FunSuite

class BattleResultSuite extends FunSuite {
  test("success combined battle_result") {
    val stream = getClass.getResource("/combined_battle_result.json").openStream()
    val json = try {
      parse(stream)
    } finally {
      stream.close()
    }
    val result = BattleResult.fromJson(json \ "api_data")
    assert(result.winRank === "S")
    assert(result.exp === 3500)
    assert(result.mvp === 4)
    assert(result.baseExp === 500)
    assert(result.shipExp === List(900, 600, 600, 1200, 600, 600))
    assert(result.lostFlag === Nil)
    assert(result.firstClear === false)
    assert(result.getShip === None)
  }
}
