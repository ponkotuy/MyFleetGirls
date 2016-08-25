package tool

import models.db.{MasterShipSpecs, MasterSlotItem}
import models.join.SlotItemWithMaster

trait AirSuperiority {
  def airSuperiority: Int
}

trait AirSuperiorityWithSlot extends AirSuperiorityWithoutSlot {
  def spec: MasterShipSpecs

  def slot: Seq[SlotItemWithMaster]
  def slotMaster: Seq[MasterSlotItem]

  def slotAlvs: Seq[Int] = slot.map { s => s.alv.getOrElse(1) }
}

trait AirSuperiorityWithoutSlot extends AirSuperiority {
  def spec: MasterShipSpecs

  def slotAlvs: Seq[Int]
  def slotMaster: Seq[MasterSlotItem]

  /** 制空値計算。大きく分けて艦載機性能とスロットに依る部分と、練度に依る部分に分かれる */
  override def airSuperiority: Int = {
    import EquipType._
    (slotAlvs, slotMaster, spec.maxeq).zipped.collect {
      case (alv, sm, slotCount) if sm.category.exists(CarrierBased.contains) =>
        val fromAlv = for {
          b <- sm.category.collect {
            case Fighter | SeaplaneFighter => 25
            case Bomber | TorpedoBomber => 3
            case SeaBasedBomber => 9
          }
        } yield (alv - 1) * b / 6
        Math.floor(sm.antiair * math.sqrt(slotCount)).toInt + fromAlv.getOrElse(0)
    }.sum
  }
}
