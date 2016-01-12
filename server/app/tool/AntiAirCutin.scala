package tool

import models.db.MasterSlotItem

trait AntiAirCutin {
  import AntiAirCutin._
  import com.ponkotuy.value.ShipIds._

  def shipId: Int
  def stName: String
  def slotMaster: Seq[MasterSlotItem]

  lazy val antiAirCutin: Int = {
    if(isMayaMk2Cutin) {
      if(ids.exists(AntiAirRadar.contains)) 8 else 6
    }
    else if(akidukiType > 0) akidukiType
    else if(isIsuzuMk2Cutin) 3
    else if(isBattleshipCutin) {
      if(ids.exists(AntiAirRadar.contains)) 6 else 4
    }
    else {
      val hasWithGun = ids.exists(SystemWithGun.contains)
      val hasRadar = ids.exists(AntiAirRadar.contains)
      val countCaliber = categories.count(_ == EquipType.AntiAirGun) // 対空機銃
      val hasGun = icons.contains(EquipIconType.AntiAirGun) // 高角砲
      val hasSystem = ids.exists(AntiAirSystem.contains)
      val hasManyGun = ids.exists(ManyAntiAirGun.contains)
      if(hasWithGun && hasRadar) 4
      else if(hasGun && hasSystem && hasRadar) 3
      else if(hasManyGun && 2 <= countCaliber && hasRadar) 3
      else if(hasGun && hasSystem) 2
      else 0
    }
  }

  def antiAirCutinOpt: Option[Int] = if(antiAirCutin == 0) None else Some(antiAirCutin)

  private def ids = slotMaster.map(_.id)
  private def categories = slotMaster.flatMap(_.category)
  private def icons = slotMaster.flatMap(_.iconType)

  private def isMayaMk2Cutin: Boolean =
    shipId == MayaMk2 &&
        icons.contains(EquipIconType.AntiAirGun) && // 高角砲
        ids.exists(ManyAntiAirGun.contains)

  private def akidukiType: Int = {
    if(!AkidukiType.contains(shipId)) return 0
    val gunCount = icons.count(_ == EquipIconType.AntiAirGun)
    val isRadar = categories.exists(EquipType.Radars.contains(_))
    gunCount match {
      case 2 if isRadar => 7
      case 1 if isRadar => 6
      case 3 => 4
      case 2 => 4
      case _ => 0
    }
  }

  private def isIsuzuMk2Cutin: Boolean =
    shipId == IsuzuMk2 &&
        icons.contains(EquipIconType.AntiAirGun) && // 高角砲
        (
          categories.contains(EquipType.AntiAirGun) && // 対空機銃
          ids.exists(AntiAirRadar.contains)
        ) || ids.exists(AntiAirSystem.contains)

  private def isBattleshipCutin: Boolean =
    Battleship.contains(stName) &&
        categories.contains(EquipType.MainGunL) &&
        categories.contains(EquipType.AntiAirBullet) &&
        ids.exists(AntiAirSystem.contains)
}

object AntiAirCutin {
  import com.ponkotuy.value.ShipIds._

  val AkidukiType = Set(Akiduki, Teruduki, AkidukiMk1, TerudukiMk1) // 秋月型
  val ManyAntiAirGun = Set(131) // 25mm三連装機銃 集中配備など
  val AntiAirRadar = Set(27, 30, 32, 89, 106) // 対空電探
  val SystemWithGun = Set(122, 130) // 高角砲 + 高射装置
  val AntiAirSystem = Set(120, 121, 122, 130) // 高射装置を含む
  val Battleship = Set("戦艦", "航空戦艦")
}
