package com.ponkotuy.data

import com.ponkotuy.data.master.MasterShip
import org.scalatest.FunSuite
import org.json4s.native.JsonMethods._

class MasterShipSuite extends FunSuite {
  val stream = getClass.getResource("/api_start2.json").openStream()
  val json = try {
    parse(stream) \ "api_data"
  } finally {
    stream.close()
  }

  test("MasterShip from 2015/07/24 pattern") {
    val value = MasterShip.fromJson(json \ "api_mst_ship", _ => "")
    assert(value.nonEmpty)
  }
}
