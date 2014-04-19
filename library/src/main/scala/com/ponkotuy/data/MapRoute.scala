package com.ponkotuy.data

/**
 *
 * @author ponkotuy
 * Date: 14/04/19.
 */
case class MapRoute(areaId: Int, infoNo: Int, dep: Int, dest: Int, fleet: Seq[Int])

object MapRoute {
  def fromMapNext(dep: MapStart, dest: MapStart, fleet: Seq[Int]): MapRoute = {
    require(dep.mapAreaId == dest.mapAreaId)
    require(dep.mapInfoNo == dest.mapInfoNo)
    MapRoute(dep.mapAreaId, dep.mapInfoNo, dep.no, dest.no, fleet)
  }
}
