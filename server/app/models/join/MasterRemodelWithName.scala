package models.join

import models.db._
import scalikejdbc._

import scala.collection.breakOut

/**
 *
 * @author ponkotuy
 * Date: 15/02/14.
 */
case class MasterRemodelWithName(
    master: MasterRemodel,
    slotitem: MasterSlotItem,
    use: Option[MasterSlotItem],
    to: Option[RemodelWithName],
    secondShip: MasterShipBase)

object MasterRemodelWithName {
  val MaxLevel = 10
  def apply(
      master: MasterRemodel,
      slotitem: MasterSlotItem,
      use: Option[MasterSlotItem],
      secondShip: MasterShipBase): MasterRemodelWithName = {
    val to = {
      if(master.slotitemLevel == MaxLevel) {
        val where = sqls"r.before_item_level = ${MaxLevel}"
          .and append sqls"r.before_item_id = ${master.slotitemId}"
          .and append sqls"ras.id is not null"
        Remodel.findAllByWithName(where, limit = 1).headOption
      } else None
    }
    MasterRemodelWithName(master, slotitem, use, to, secondShip)
  }
}

case class MasterRemodelJson(
    master: MasterRemodel,
    slotitem: MasterSlotItem,
    use: Option[MasterSlotItem],
    to: Option[RemodelWithName],
    secondShip: String)

object MasterRemodelJson {
  def fromWithName(xs: Iterable[MasterRemodelWithName]): Vector[MasterRemodelJson] = {
    val result: Vector[MasterRemodelJson] = xs.groupBy { x => (x.master.slotitemId, x.master.slotitemLevel) }.values.flatMap { ys =>
      val groups = ys.groupBy(_.to.map(_.after.id)).values
      groups.map { zs =>
        val seconds = if(groups.size <= 1) "全て" else zs.map(_.secondShip.name).mkString(", ")
        val z = zs.head
        MasterRemodelJson(z.master, z.slotitem, z.use, z.to, seconds)
      }
    }(breakOut)
    result.sortBy(_.master.slotitemLevel)
  }
}
