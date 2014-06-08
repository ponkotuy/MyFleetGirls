package com.ponkotuy.data

import com.ponkotuy.tool.Pretty

/**
 *
 * @author ponkotuy
 * Date: 14/04/19.
 */
case class MapRoute(areaId: Int, infoNo: Int, dep: Int, dest: Int, fleet: Seq[Int]) {
  def summary: String = Pretty(
    Map(
      ("Map", s"$areaId-$infoNo"),
      ("Route", s"from $dep to $dest"),
      ("艦隊", fleet.mkString("(", ", ", ")")))
  )
}

object MapRoute {
  def fromMapNext(dep: MapStart, dest: MapStart, fleet: Seq[Int]): MapRoute = {
    require(dep.mapAreaId == dest.mapAreaId)
    require(dep.mapInfoNo == dest.mapInfoNo)
    MapRoute(dep.mapAreaId, dep.mapInfoNo, dep.no, dest.no, fleet)
  }
}
