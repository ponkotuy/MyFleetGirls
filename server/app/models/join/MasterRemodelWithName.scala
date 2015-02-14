package models.join

import models.db._
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/02/14.
 */
case class MasterRemodelWithName(master: MasterRemodel, slotitem: MasterSlotItem, use: Option[MasterSlotItem], to: Option[RemodelWithName])

object MasterRemodelWithName {
  val MaxLevel = 10
  def apply(master: MasterRemodel, slotitem: MasterSlotItem, use: Option[MasterSlotItem]): MasterRemodelWithName = {
    val to = {
      if(master.slotitemLevel == MaxLevel) {
        val where = sqls"r.before_item_level = ${MaxLevel}"
          .and append sqls"r.before_item_id = ${master.slotitemId}"
          .and append sqls"ras.id is not null"
        Remodel.findAllByWithName(where, limit = 1).headOption
      } else None
    }
    MasterRemodelWithName(master, slotitem, use, to)
  }
}
