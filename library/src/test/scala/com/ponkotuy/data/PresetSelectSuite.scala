package com.ponkotuy.data

import org.json4s.native.JsonMethods._
import org.scalatest.FunSuite

class PresetSelectSuite extends FunSuite {
  test("success pattern") {
    val stream = getClass.getResource("/preset_select.json").openStream()
    val json = try {
      parse(stream)
    } finally {
      stream.close()
    }
    val result = PresetSelect.fromJson(json \ "api_data")
    assert(result.isDefined)
    val res = result.get
    assert(res.memberId === 10007732)
    assert(res.ship === Seq(188, 505, 126, 884))
    assert(res.flagship === 0)
  }
}
