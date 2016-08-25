package tool

import models.db.{MasterShipSpecs, MasterSlotItem}
import models.join.SlotItemWithMaster

trait AirSuperiority {
  def airSuperiority: Int
}

object AirSuperiority {

  import EquipType._

  /** 航空戦参加機体 */
  val participants: Set[EquipType] = Set(
    Fighter, Bomber, TorpedoBomber, SeaBasedBomber, SeaplaneFighter
  )

  private val innerProf = Vector(0, 10, 25, 40, 55, 70, 85, 100).lift

  def innerProficiencyBonus(alv: Int): Option[Double] =
    innerProf(alv).map(p => Math.sqrt(p / 10.0))

  private val fighterBonus = Vector(0, 0, 2, 5, 9, 14, 14, 22).lift
  private val sBomberBonus = Vector(0, 0, 1, 1, 1, 3, 3, 6).lift

  def categoryBonus(category: EquipType, alv: Int): Option[Int] =
    category match {
      case Fighter | SeaplaneFighter => fighterBonus(alv)
      case SeaBasedBomber => sBomberBonus(alv)
      case _ => None
    }

}

trait AirSuperiorityWithSlot extends AirSuperiorityWithoutSlot {
  def spec: MasterShipSpecs

  def slot: Seq[SlotItemWithMaster]
  def slotMaster: Seq[MasterSlotItem]

  def slotAlvs: Seq[Int] = slot.map { s => s.alv.getOrElse(0) }
}

trait AirSuperiorityWithoutSlot extends AirSuperiority {
  def spec: MasterShipSpecs

  def slotAlvs: Seq[Int]
  def slotMaster: Seq[MasterSlotItem]

  /** 制空値計算。大きく分けて艦載機性能とスロットに依る部分と、練度に依る部分に分かれる */
  override def airSuperiority: Int = {
    import AirSuperiority._
    val airSups = (slotAlvs, slotMaster, spec.maxeq).zipped.collect {
      case (alv, sm, slotCount) if sm.category.exists(participants) =>
        val base = sm.antiair * math.sqrt(slotCount)
        val profBonus = innerProficiencyBonus(alv).getOrElse(0.0)
        val catBonus = sm.category.flatMap(categoryBonus(_, alv)).getOrElse(0)
        (base + profBonus + catBonus).toInt
    }
    airSups.sum
  }
}
