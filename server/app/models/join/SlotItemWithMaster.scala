package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 14/10/22.
 */
case class SlotItemWithMaster(item: SlotItem, master: MasterSlotItem) {
  def id = master.id
  def name = master.name
  def category = master.category
  def iconType = master.iconType

  def colorClass: String = {
    import tool.EquipIconType._
    iconType.map {
      case MainGunS | MainGunM | MainGunL | AntiShipBullet | Bomber => "danger"
      case CaliberGun | AntiAirBullet | AntiAirGun | Fighter | ScoutSeaplane | Autogiro => "success"
      case SubGun | Scout | Radar | EngineChamber | Searchlight | Flare => "warning"
      case Torpedo | TorpedoBomber | Sonar | DepthBomb => "info"
      case LandingCraft => "success"
      case MaritimePartrolAircraft => "info"
      case Armor => "purple"
      case DamageControl | SimplifiedPackage | RepairFacility | Pilot | Command => ""
    }
  }.getOrElse("")
}
