package models.join

import models.db._
import tool.{EquipType, ShipExperience}

/**
 * Date: 14/06/16.
 */
trait ShipParameter extends GraphData {
  import models.join.ShipParameter._

  def ship: Ship
  def master: MasterShipBase
  def stype: MasterStype
  def spec: MasterShipSpecs

  def id = ship.id
  def shipId = ship.shipId
  def memberId = ship.memberId
  def lv = ship.lv
  def exp = ship.exp
  def nowhp = ship.nowhp
  def maxhp = ship.maxhp
  def fuel = ship.fuel
  def bull = ship.bull
  def dockTime = ship.dockTime
  def cond = ship.cond
  def karyoku = ship.karyoku
  def raisou = ship.raisou
  def taiku = ship.taiku
  def soukou = ship.soukou
  def kaihi = ship.kaihi
  def taisen = ship.taisen
  def sakuteki = ship.sakuteki
  def lucky = ship.lucky
  def locked = ship.locked
  def created = ship.created
  def name = master.name
  def yomi = master.yomi
  def stName = stype.name
  def stAbbName = stAbbNames(stName)

  def isDamaged = nowhp <= (maxhp / 2)
  def damage: Option[Damage] = Damage.fromHp(nowhp, maxhp)

  lazy val slot: Seq[SlotItemWithMaster] = SlotItem.findIn(ship.slot, memberId)
  lazy val slotMaster: Seq[MasterSlotItem] = {
    val ids = slot.map(_.slotitemId)
    val map = MasterSlotItem.findIn(ids).map(it => it.id -> it).toMap
    ids.flatMap(map.get)
  }

  /** 制空値計算。大きく分けて艦載機性能とスロットに依る部分と、練度に依る部分に分かれる */
  lazy val airSuperiority: Int = {
    val fromAlvs = for {
      x <- slot
      alv <- x.alv
    } yield (alv - 1) * 25 / 6
    slotMaster.zip(spec.maxeq)
        .filter(_._1.category.exists(EquipType.CarrierBased.contains))
        .map { case (fighter, slotCount) =>
      Math.floor(fighter.antiair * math.sqrt(slotCount)).toInt
    }.sum + fromAlvs.sum
  }

  def slotNames: Seq[String] = slot.map(_.nameWithLevel)

  def hpRate: Double = nowhp / maxhp.toDouble

  def rowKaryoku: Int = karyoku - slotMaster.map(_.power).sum
  def rowRaisou: Int = raisou - slotMaster.map(_.torpedo).sum
  def rowTaiku: Int = taiku - slotMaster.map(_.antiair).sum
  def rowSoukou: Int = soukou

  def calcRate(row: Double, min: Double, max: Double) = if(max == min) 1.0 else (row - min)/(max - min)
  def karyokuRate: Double = calcRate(rowKaryoku, spec.karyokuMin, spec.karyokuMax)
  def raisouRate: Double = calcRate(rowRaisou, spec.raisouMin, spec.raisouMax)
  def taikuRate: Double = calcRate(rowTaiku, spec.taikuMin, spec.taikuMax)
  def soukouRate: Double = calcRate(rowSoukou, spec.soukoMin, spec.soukoMax)

  /** 運の改修度 */
  def upLucky: Int = lucky - spec.luckyMin

  /** Condition値による色の変化 */
  def rgb: RGB = ShipParameter.rgb(cond)
  def condBarRGB: RGB = ShipParameter.condBarRGB(cond)

  /** HPによる色の変化 */
  def hpRGB: RGB = ShipParameter.hpRGB(hpRate)

  /** 次のLvまでに必要な経験値の取得率 */
  def expRate: Double = (exp - ShipExperience.sum(lv)).toDouble/ShipExperience.diff(lv + 1)
  /** LvMAX(100 or 150)までに必要な経験値の取得率 */
  def entireExpRate: Double =
    if(lv > 99) exp.toDouble/ShipExperience.sum(150) else exp.toDouble/ShipExperience.sum(100)
}

object ShipParameter {
  val stAbbNames = Map(
    "重雷装巡洋艦" -> "雷巡", "重巡洋艦" -> "重巡", "軽巡洋艦" -> "軽巡",
    "航空巡洋艦" -> "航巡", "航空戦艦" -> "航戦",
    "水上機母艦" -> "水母"
  ).withDefault(identity)

  case class RGB(r: Int, g: Int, b: Int) {
    def blend(other: RGB, rate: Double): RGB = {
      def f(x: Int, y: Int, yrate: Double): Int = (x * (1.0 - yrate) + y * yrate).toInt
      RGB(f(r, other.r, rate),
        f(g, other.g, rate),
        f(b, other.b, rate))
    }

    override def toString = f"#$r%2X$g%2X$b%2X"
  }

  val Red = RGB(242, 222, 222)
  val Blue = RGB(217, 237, 247)
  val White = RGB(231, 227, 223)
  val Yellow = RGB(252, 251, 227)

  def rgb(cond: Int): RGB = {
    if(cond > 49) Blue
    else White.blend(Red, (49.0 - cond) / 49.0)
  }

  def condBarRGB(cond: Int): RGB = {
    if(cond > 49) Blue
    else Yellow.blend(Red, (49.0 - cond) / 49.0)
  }

  def hpRGB(rate: Double): RGB = {
    if(rate > 0.5) Yellow.blend(Blue, (rate - 0.5) * 2.0)
    else Red.blend(Yellow, rate * 2.0)
  }
}

sealed abstract class Damage(val name: String)

object Damage {
  case object Minor extends Damage("minor")
  case object Half extends Damage("half")
  case object Major extends Damage("major")

  def fromHp(now: Int, max: Int): Option[Damage] =
    if(now <= max / 4) Some(Major)
    else if(now <= max / 2) Some(Half)
    else if(now <= max * 3 / 4) Some(Minor)
    else None
}
