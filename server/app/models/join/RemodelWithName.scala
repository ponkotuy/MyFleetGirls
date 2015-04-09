package models.join

import models.db._
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/02/04.
 */
case class RemodelWithName(
    remodel: Remodel,
    before: MasterSlotItem,
    after: MasterSlotItem,
    remodelAfter: Option[RemodelAfterSlot],
    secondShip: Option[MasterShipBase],
    master: Option[MasterRemodel],
    use: Option[MasterSlotItem])

object RemodelWithName {
  def apply(
      remodel: Remodel,
      before: MasterSlotItem,
      after: MasterSlotItem,
      remodelAfter: Option[RemodelAfterSlot],
      secondShip: Option[MasterShipBase]): RemodelWithName = {
    val master = findMaster(remodel, secondShip)
    val use = master.flatMap { mr => MasterSlotItem.find(mr.useSlotitemId) }
    RemodelWithName(remodel, before, after, remodelAfter, secondShip, master, use)
  }

  def findMaster(remodel: Remodel, secondShip: Option[MasterShipBase]): Option[MasterRemodel] = {
    val mr = MasterRemodel.mr
    val result = MasterRemodel.findAllBy(
      sqls.eq(mr.slotitemId, remodel.beforeItemId).and.eq(mr.slotitemLevel, remodel.beforeItemLevel)
    )
    val opt = for {
      second <- secondShip.map(_.id)
      hit <- result.find(_.secondShipId == second)
    } yield hit
    opt.orElse(result.headOption)
  }
}
