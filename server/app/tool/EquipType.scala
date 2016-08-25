package tool

/**
 *
 * @author ponkotuy
 * Date: 14/10/22.
 */

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
  case object AntiAirSystem extends EquipType(36)
  case object AntiSurface extends EquipType(37)
  case object Crew extends EquipType(39)
  case object SonarL extends EquipType(40)
  case object SeaplaneL extends EquipType(41)
  case object SearchlightL extends EquipType(42)
  case object Meal extends EquipType(43)
  case object Supply extends EquipType(44)
  case object SeaplaneFighter extends EquipType(45)

  val values = Array(MainGunS, MainGunM, MainGunL, SubGun, Torpedo, Fighter, Bomber, TorpedoBomber, Scout,
    ScoutSeaplane, SeaBasedBomber, RadarS, RadarL, Sonar, DepthBomb, Armor, EngineChamber, AntiAirBullet,
    AntiShipBullet, ProximityFuze, AntiAirGun, MidgetSubmarine, DamageControl, LandingCraft, Autogiro,
    MaritimePartrolAircraft, ArmorM, ArmorL, Searchlight, SimplifiedPackage, RepairFacility, ShortTorpedo, Flare,
    Command, Pilot, AntiAirSystem, AntiSurface, Crew, SonarL, SeaplaneL, SearchlightL, Meal, Supply, SeaplaneFighter)
  def fromInt(v: Int): Option[EquipType] = values.find(_.v == v)

  val Scouts: Array[EquipType] = Array(Scout, ScoutSeaplane)
  val Radars: Array[EquipType] = Array(RadarS, RadarL)
  val Aircraft: Array[EquipType] = Array(
    Fighter, Bomber, TorpedoBomber, Scout, ScoutSeaplane, SeaBasedBomber, MaritimePartrolAircraft, SeaplaneFighter
  )
  val CarrierBased: Array[EquipType] = Array(Fighter, Bomber, TorpedoBomber, SeaBasedBomber, SeaplaneFighter)
}

sealed abstract class EquipIconType(val v: Int)
object EquipIconType {
  case object MainGunS extends EquipIconType(1)
  case object MainGunM extends EquipIconType(2)
  case object MainGunL extends EquipIconType(3)
  case object SubGun extends EquipIconType(4)
  case object Torpedo extends EquipIconType(5)
  case object Fighter extends EquipIconType(6)
  case object Bomber extends EquipIconType(7)
  case object TorpedoBomber extends EquipIconType(8)
  case object Scout extends EquipIconType(9)
  case object ScoutSeaplane extends EquipIconType(10)
  case object Radar extends EquipIconType(11)
  case object AntiAirBullet extends EquipIconType(12)
  case object AntiShipBullet extends EquipIconType(13)
  case object DamageControl extends EquipIconType(14)
  case object CaliberGun extends EquipIconType(15)
  case object AntiAirGun extends EquipIconType(16)
  case object DepthBomb extends EquipIconType(17)
  case object Sonar extends EquipIconType(18)
  case object EngineChamber extends EquipIconType(19)
  case object LandingCraft extends EquipIconType(20)
  case object Autogiro extends EquipIconType(21)
  case object MaritimePartrolAircraft extends EquipIconType(22)
  case object Armor extends EquipIconType(23)
  case object Searchlight extends EquipIconType(24)
  case object SimplifiedPackage extends EquipIconType(25)
  case object RepairFacility extends EquipIconType(26)
  case object Flare extends EquipIconType(27)
  case object Command extends EquipIconType(28)
  case object Pilot extends EquipIconType(29)

  val values = Array(MainGunS, MainGunM, MainGunL, SubGun, Torpedo, Fighter, Bomber, TorpedoBomber, Scout,
    ScoutSeaplane, Radar, AntiAirBullet, AntiShipBullet, DamageControl, CaliberGun, AntiAirGun, DepthBomb, Sonar,
    EngineChamber, LandingCraft, Autogiro, MaritimePartrolAircraft, Armor, Searchlight, SimplifiedPackage, RepairFacility,
    Flare, Command, Pilot)
  def fromInt(v: Int): Option[EquipIconType] = values.find(_.v == v)
}
