package models.join

import models.db.{FleetGroupMember, FleetGroup}
import scalikejdbc._
import scala.collection.breakOut

/**
 *
 * @author ponkotuy
 * Date: 15/03/22.
 */
case class FleetGroupWithShip(group: FleetGroup, ships: Seq[Int])

object FleetGroupWithShip {
  def findAll()(implicit session: DBSession = AutoSession): List[FleetGroupWithShip] = {
    val fgs: Map[Long, FleetGroup] = FleetGroup.findAll().map(it => it.id -> it)(breakOut)
    val ships = FleetGroupMember.findAll()
    ships.groupBy(_.groupId).map { case (groupId, xs) =>
      FleetGroupWithShip(fgs(groupId), xs.map(_.shipId))
    }(breakOut)
  }
}
