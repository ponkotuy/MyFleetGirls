package tool

import models.db.{MasterShipSpecs, MasterSlotItem}
import models.join.SlotItemWithMaster

trait AirSuperiority {
  def airSuperiority: Int
}

trait AirSuperiorityBase extends AirSuperiority {

  def fighterPowers: Seq[Option[FighterPower]]

  override def airSuperiority: Int = fighterPowers.flatten.map(_.value).sum

}

trait AirSuperiorityWithSlot extends AirSuperiorityBase {
  def spec: MasterShipSpecs

  def slot: Seq[SlotItemWithMaster]

  override def fighterPowers: Seq[Option[FighterPower]] =
    slot.zip(spec.maxeq).map {
      case (s, count) => FighterPower.from(s, count)
    }

}

trait AirSuperiorityWithoutSlot extends AirSuperiorityBase {
  def spec: MasterShipSpecs

  def slotMaster: Seq[MasterSlotItem]

  def slotLevels: Seq[Int]

  def slotAlvs: Seq[Int]

  override def fighterPowers: Seq[Option[FighterPower]] =
    (slotMaster, spec.maxeq, slotLevels.zip(slotAlvs)).zipped.map {
      case (sm, count, (level, alv)) => FighterPower.from(sm, count, level, alv)
    }

}


case class FighterPower(
    category: EquipType,
    antiAir: Int,
    slotCount: Int,
    level: Int,
    alv: Int) {

  import FighterPower._

  def value: Int = {
    val imp = improvementBonus(category, level).getOrElse(0.0)
    val base = (antiAir + imp) * math.sqrt(slotCount)
    val prof = proficiencyBonus(alv).getOrElse(0.0)
    val cat = categoryBonus(category, alv).getOrElse(0)
    (base + prof + cat).toInt
  }

}

object FighterPower {

  def from(master: MasterSlotItem, slotCount: Int, level: Int, alv: Int): Option[FighterPower] =
    PartialFunction.condOpt(master.category) {
      case Some(cat) if participants(cat) =>
        FighterPower(cat, master.antiair, slotCount, level, alv)
    }

  def from(slot: SlotItemWithMaster, slotCount: Int): Option[FighterPower] =
    from(slot.master, slotCount, slot.level, slot.alv.getOrElse(0))


  import EquipType._

  /** 航空戦参加機体 */
  private val participants: Set[EquipType] = Set(
    Fighter, Bomber, TorpedoBomber, SeaBasedBomber, SeaplaneFighter
  )

  private val improvementBonus = Map(
    Fighter -> 0.2,
    Bomber -> 0.25
  )

  def improvementBonus(category: EquipType, level: Int): Option[Double] =
    improvementBonus.get(category).map(_ * level)

  private val innerProficiency = Vector(0, 10, 25, 40, 55, 70, 85, 100).lift

  def proficiencyBonus(alv: Int): Option[Double] =
    innerProficiency(alv).map(p => Math.sqrt(p / 10.0))

  private val fighterBonus = Vector(0, 0, 2, 5, 9, 14, 14, 22).lift
  private val sBomberBonus = Vector(0, 0, 1, 1, 1, 3, 3, 6).lift

  def categoryBonus(category: EquipType, alv: Int): Option[Int] =
    category match {
      case Fighter | SeaplaneFighter => fighterBonus(alv)
      case SeaBasedBomber => sBomberBonus(alv)
      case _ => None
    }

}
