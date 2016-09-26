package com.ponkotuy.data

import org.json4s.JsonAST.JNull
import org.json4s.native.JsonMethods._
import org.scalatest.FunSuite

class ShipSuite extends FunSuite {
  test("success pattern") {
    val stream = getClass.getResource("/ship.json").openStream()
    val json = try {
      parse(stream)
    } finally {
      stream.close()
    }
    val result = Ship.fromJson(json)
    assert(result.size == 124)
    val first = result.head
    assert(first.id == 1)
    assert(first.shipId == 420)
    assert(first.exp == 414709)
    assert(first.karyoku == 59)
    assert(first.locked)
  }

  test("null pattern") {
    val result = Ship.fromJson(JNull)
    assert(result.isEmpty)
  }
}
