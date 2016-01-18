package tool

import models.db.{MasterShipSpecs, MasterSlotItem}
import models.join.SlotItemWithMaster

trait AirSuperiority {
  def airSuperiority: Int
}

trait AirSuperiorityWithSlot extends AirSuperiority {
  def spec: MasterShipSpecs

  def slot: Seq[SlotItemWithMaster]
  def slotMaster: Seq[MasterSlotItem]

  /** 制空値計算。大きく分けて艦載機性能とスロットに依る部分と、練度に依る部分に分かれる */
  override def airSuperiority: Int = {
    import EquipType._
    (slot, slotMaster, spec.maxeq).zipped.collect {
      case (s, sm, slotCount) if sm.category.exists(CarrierBased.contains) =>
        val fromAlv = for {
          a <- s.alv
          b <- sm.category.collect {
            case Fighter => 25
            case Bomber | TorpedoBomber => 3
            case SeaBasedBomber => 9
          }
        } yield (a - 1) * b / 6
        Math.floor(sm.antiair * math.sqrt(slotCount)).toInt + fromAlv.getOrElse(0)
    }.sum
  }
}

trait AirSuperiorityWithoutSlot extends AirSuperiority {
  def spec: MasterShipSpecs

  def slotMaster: Seq[MasterSlotItem]

  override def airSuperiority: Int = {
    import EquipType._
    (slotMaster, spec.maxeq).zipped.collect {
      case (sm, slotCount) if sm.category.exists(CarrierBased.contains) =>
        Math.floor(sm.antiair * math.sqrt(slotCount)).toInt
    }.sum
  }
}
