package models.join

import models.db.MasterSlotItem

trait WithSlot {
  def slot: Seq[SlotItemWithMaster]

  lazy val slotMaster: Seq[MasterSlotItem] = {
    val ids = slot.map(_.slotitemId)
    val map = MasterSlotItem.findIn(ids).map(it => it.id -> it).toMap
    ids.flatMap(map.get)
  }

  def slotNames: Seq[String] = slot.map(_.nameWithLevel)
}
