package com.ponkotuy.restype

import org.scalatest.FunSuite

class FleetsStateSuite extends FunSuite {
  test("setFleets") {
    FleetsState.clear()
    val data = Seq(Seq(1, 2, 3), Seq(4, 5, 6))
    FleetsState.setFleets(data)
    assert(FleetsState.toSeq.map(_.toSeq) === data)
  }

  test("getFleet") {
    FleetsState.clear()
    val data = Seq(Seq(1, 2, 3), Seq(4, 5, 6))
    FleetsState.setFleets(data)
    assert(FleetsState.getFleet(1).map(_.toSeq) === Some(Seq(4, 5, 6)))
    assert(FleetsState.getFleet(3) === None)
  }

  test("findShipIdx") {
    FleetsState.clear()
    val data = Seq(Seq(1, 2, 3), Seq(4, 5, 6))
    FleetsState.setFleets(data)
    assert(FleetsState.findShipIdx(6) === Some((1, 2)))
    assert(FleetsState.findShipIdx(-1) === None)
  }
}
