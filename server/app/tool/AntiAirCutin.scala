package tool

import models.db.MasterSlotItem

trait AntiAirCutin {
  import AntiAirCutin._
  import com.ponkotuy.value.ShipIds._

  def shipId: Int
  def stName: String
  def slotMaster: Seq[MasterSlotItem]

  def antiAirCutin: Int = antiAirCutinOpt.getOrElse(0)

  lazy val antiAirCutinOpt: Option[Int] =
    mayaMk2Cutin
        .orElse(akidukiType)
        .orElse(isuzuMk2Cutin)
        .orElse(kasumiCutin)
        .orElse(satsukiCutin)
        .orElse(battleshipCutin)
        .orElse(commonCutin)

  private def mayaMk2Cutin: Option[Int] = {
    if(shipId != MayaMk2) return None
    if(countGun > 0 && hasManyGun) {
      Some(if (hasAntiAirRadar) 8 else 6)
    } else None
  }

  private def akidukiType: Option[Int] = {
    if(!AkidukiType.contains(shipId)) return None
    countGun match {
      case 2 if hasRadar => Some(7)
      case 1 if hasRadar => Some(6)
      case 3 | 2 => Some(4)
      case _ => None
    }
  }

  private def isuzuMk2Cutin: Option[Int] = {
    if(shipId != IsuzuMk2) return None
    if(countGun > 0 && countCaliber > 0) {
      Some(if(hasAntiAirRadar) 4 else 3)
    } else None
  }

  private def battleshipCutin: Option[Int] = {
    if(!Battleship.contains(stName)) return None
    if(hasMainGun && hasAntiAirBullet && hasSystem) {
      Some(if(hasAntiAirRadar) 6 else 4)
    } else None
  }

  private def kasumiCutin: Option[Int] = {
    if(shipId != KasumiMk2Otsu) return None
    if(countGun > 0 && countCaliber > 0) {
      Some(if(hasAntiAirRadar) 4 else 2)
    } else None
  }

  private def satsukiCutin: Option[Int] = {
    if(shipId != SatsukiMk2) return None
    if(hasManyGun) Some(2) else None
  }

  private def commonCutin: Option[Int] = {
    if(hasWithGun && hasAntiAirRadar) Some(4)
    else if(countGun > 0 && hasSystem && hasAntiAirRadar) Some(3)
    else if(hasManyGun && 2 <= countCaliber && hasAntiAirRadar) Some(3)
    else if(countGun > 0 && hasSystem) Some(2)
    else None
  }

  private lazy val ids = slotMaster.map(_.id)
  private lazy val categories = slotMaster.flatMap(_.category)
  private lazy val icons = slotMaster.flatMap(_.iconType)

  /** 対空機銃 */
  private lazy val countCaliber = categories.count(_ == EquipType.AntiAirGun)
  /** 高角砲 */
  private lazy val countGun = icons.count(_ == EquipIconType.AntiAirGun)
  /** レーダー */
  private lazy val hasRadar = categories.exists(EquipType.Radars.contains(_))
  /** 対空レーダー */
  private lazy val hasAntiAirRadar = ids.exists(AntiAirRadar.contains)
  /** 集中配備機銃 */
  private lazy val hasManyGun = ids.exists(ManyAntiAirGun.contains)
  /** 高射装置 */
  private lazy val hasSystem = ids.exists(AntiAirSystem.contains)
  /** 高角砲+高射装置 */
  private lazy val hasWithGun = ids.exists(SystemWithGun.contains)
  /** 大口径主砲 */
  private lazy val hasMainGun = categories.contains(EquipType.MainGunL)
  /** 三式弾 */
  private lazy val hasAntiAirBullet = categories.contains(EquipType.AntiAirBullet)
}

object AntiAirCutin {
  import com.ponkotuy.value.ShipIds._

  val AkidukiType = Set(Akiduki, Teruduki, Hatsuduki, AkidukiMk1, TerudukiMk1, HatsudukiMk1) // 秋月型
  val ManyAntiAirGun = Set(131, 173, 191) // 25mm三連装機銃 集中配備など
  val AntiAirRadar = Set(27, 30, 32, 89, 106) // 対空電探
  val SystemWithGun = Set(122, 130, 135) // 高角砲 + 高射装置
  val AntiAirSystem = Set(120, 121, 122, 130, 135) // 高射装置を含む
  val Battleship = Set("戦艦", "航空戦艦")
}
