package tool

import dat.ShipParameter

/**
 *
 * @author ponkotuy
 * Date: 14/10/14.
 */
object Calc {
  import EquipType._

  val OtherAircraft = Set(Aircraft: _*) -- Set(Scouts: _*)

  def spotter(ships: Iterable[ShipParameter]): Int = {
    val spots = ships.flatMap { ship =>
      ship.slotMaster.flatMap { slot => slot.category.map { cat => cat -> slot.search}}
    }
    val scots = spots.filter { case (cat, _) => Scouts.contains(cat)}.map(_._2).sum
    val radars = spots.filter { case (cat, _) => Radars.contains(cat)}.map(_._2).sum
    val aircrafts = spots.filter { case (cat, _) => OtherAircraft.contains(cat) }.map(_._2).sum
    val sum = ships.map(_.sakuteki).sum
    scots*2 + radars + aircrafts*2 + math.sqrt(sum - radars - scots).toInt
  }
}

sealed abstract class EquipType(val v: Int)

object EquipType {
  case object MainGunS extends EquipType(1)
  case object MainGunM extends EquipType(2)
  case object MainGunL extends EquipType(3)
  case object SubGun extends EquipType(4)
  case object Torpedo extends EquipType(5)
  case object Fighter extends EquipType(6)
  case object Bomber extends EquipType(7)
  case object TorpedoBomber extends EquipType(8)
  case object Scout extends EquipType(9)
  case object ScoutSeaplane extends EquipType(10)
  case object SeaBasedBomber extends EquipType(11)
  case object RadarS extends EquipType(12)
  case object RadarL extends EquipType(13)
  case object Sonar extends EquipType(14)
  case object DepthBomb extends EquipType(15)
  case object Armor extends EquipType(16)
  case object EngineChamber extends EquipType(17)
  case object AntiAirBullet extends EquipType(18)
  case object AntiShipBullet extends EquipType(19)
  case object ProximityFuze extends EquipType(20)
  case object AntiAirGun extends EquipType(21)
  case object MidgetSubmarine extends EquipType(22)
  case object DamageControl extends EquipType(23)
  case object LandingCraft extends EquipType(24)
  case object Autogiro extends EquipType(25)
  case object MaritimePartrolAircraft extends EquipType(26)
  case object ArmorM extends EquipType(27)
  case object ArmorL extends EquipType(28)
  case object Searchlight extends EquipType(29)
  case object SimplifiedPackage extends EquipType(30)
  case object RepairFacility extends EquipType(31)
  case object ShortTorpedo extends EquipType(32)
  case object Flare extends EquipType(33)
  case object Command extends EquipType(34)
  case object Pilot extends EquipType(35)

  val values = Array(MainGunS, MainGunM, MainGunL, SubGun, Torpedo, Fighter, Bomber, TorpedoBomber, Scout,
    ScoutSeaplane, SeaBasedBomber, RadarS, RadarL, Sonar, DepthBomb, Armor, EngineChamber, AntiAirBullet,
    AntiShipBullet, ProximityFuze, AntiAirGun, MidgetSubmarine, DamageControl, LandingCraft, Autogiro,
    MaritimePartrolAircraft, ArmorM, ArmorL, Searchlight, SimplifiedPackage, RepairFacility, ShortTorpedo, Flare,
    Command, Pilot)
  def fromInt(v: Int): Option[EquipType] = values.find(_.v == v)

  val Scouts: Array[EquipType] = Array(Scout, ScoutSeaplane)
  val Radars: Array[EquipType] = Array(RadarS, RadarL)
  val Aircraft: Array[EquipType] = Array(
    Fighter, Bomber, TorpedoBomber, Scout, ScoutSeaplane, SeaBasedBomber, MaritimePartrolAircraft
  )
  val CarrierBased: Array[EquipType] = Array(Fighter, Bomber, Scout, MaritimePartrolAircraft)
}
