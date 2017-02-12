package com.ponkotuy.restype

import org.scalatest.FunSuite

import scala.collection.mutable

class FleetStateSuite extends FunSuite {
  def createFleet() = new FleetState(mutable.Buffer(1, 2, 3))

  test("update") {
    val fleet = createFleet()
    assert(fleet.updateShip(1, 5))
    assert(fleet.updateShip(5, 5) === false)
    assert(fleet.toSeq === Vector(1, 5, 3))
  }

  test("remove") {
    val fleet = createFleet()
    fleet.removeShip(1)
    assert(fleet.toSeq === Vector(1, 3))
  }

  test("add") {
    val fleet = createFleet()
    fleet.addShip(4)
    assert(fleet.toSeq === Vector(1, 2, 3, 4))
  }

  test("replace") {
    val fleet = createFleet()
    fleet.replace(Seq(5, 6, 7, 8))
    assert(fleet.toSeq === Vector(5, 6, 7, 8))
  }

  test("get") {
    val fleet = createFleet()
    assert(fleet.getShip(1) === Some(2))
    assert(fleet.getShip(4) === None)
  }

  test("indexOf") {
    val fleet = createFleet()
    assert(fleet.indexOf(3) === Some(2))
    assert(fleet.indexOf(4) === None)
  }

  test("size") {
    val fleet = createFleet()
    assert(fleet.size === 3)
    fleet.addShip(4)
    assert(fleet.size === 4)
  }
}
