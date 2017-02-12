package com.ponkotuy.restype

import scala.collection.mutable
import scala.collection.breakOut
import scala.util.Try

// 艦隊情報がRoute等で必要なので溜めておく
object FleetsState {
  private[this] val fleets: mutable.Buffer[FleetState] = mutable.Buffer.empty

  def clear(): Unit = synchronized { fleets.clear() }
  def setFleets(_fleets: Seq[Seq[Int]]): Unit = synchronized {
    fleets.clear()
    fleets ++= _fleets.map { fleet =>
      new FleetState(fleet.toBuffer)
    }(breakOut)
  }

  def getFleet(n: Int): Option[FleetState] = fleets.lift(n)
  def firstFleet: Option[FleetState] = getFleet(0)
  def toSeq: Seq[FleetState] = fleets.toVector

  def findShipIdx(ship: Int): Option[(Int, Int)] = fleets.zipWithIndex.flatMap { case (fleet, fleetIdx) =>
    fleet.indexOf(ship).map { idx => (fleetIdx, idx) }
  }.headOption
}

class FleetState(fleet: mutable.Buffer[Int]) {
  def updateShip(idx: Int, ship: Int): Boolean = synchronized {
    Try {
      fleet.update(idx, ship)
    }.isSuccess
  }
  def removeShip(idx: Int): Option[Int] = synchronized { Try { fleet.remove(idx) }.toOption }
  def addShip(ship: Int): Unit = synchronized { fleet.append(ship) }

  def getShip(idx: Int): Option[Int] = fleet.lift(idx)
  def firstShip: Option[Int] = getShip(0)
  def secondShip: Option[Int] = getShip(1)
  def indexOf(ship: Int): Option[Int] = {
    val raw = fleet.indexOf(ship)
    if(raw < 0) None else Some(raw)
  }
  def size: Int = fleet.size
  def toSeq: Seq[Int] = fleet.toVector
}
