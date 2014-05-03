package dat

import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import util.{ShipExperience => SE}
import models._

/**
 *
 * @author ponkotuy
 * Date: 14/04/15.
 */
case class ShipWithName(ship: Ship, master: MasterShipBase, stype: MasterStype) {
  import ShipWithName._

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

  lazy val slot: List[SlotItem] = SlotItem.findIn(ship.slot, memberId)
  lazy val slotMaster: List[MasterSlotItem] = {
    val ids = slot.map(_.slotitemId)
    val map = MasterSlotItem.findIn(ids).map(it => it.id -> it).toMap
    ids.map(map(_))
  }
  lazy val spec: MasterShipSpecs = MasterShipSpecs.find(shipId).get

  def slotNames: List[String] = slot.map(_.name)

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

  def toJson: String = {
    val seq = Seq(
      Seq(0, karyoku / 150.0),
      Seq(1, raisou / 130.0),
      Seq(2, taiku / 90.0),
      Seq(3, soukou / 100.0),
      Seq(4, kaihi / 80.0),
      Seq(5, taisen / 80.0),
      Seq(6, sakuteki / 70.0),
      Seq(7, lucky / 40.0)
    )
    compact(render(seq))
  }

  /** Condition値による色の変化 */
  def rgb: RGB = ShipWithName.rgb(cond)
  def condBarRGB: RGB = ShipWithName.condBarRGB(cond)

  /** HPによる色の変化 */
  def hpRGB: RGB = ShipWithName.hpRGB(hpRate)

  /** 次のLvまでに必要な経験値の取得率 */
  def expRate: Double = (exp - SE.sum(lv)).toDouble/SE.diff(lv + 1)
  /** LvMAX(100 or 150)までに必要な経験値の取得率 */
  def entireExpRate: Double =
    if(lv > 99) exp.toDouble/SE.sum(150) else exp.toDouble/SE.sum(100)
}

object ShipWithName {
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
  val White = RGB(255, 255, 255)
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
