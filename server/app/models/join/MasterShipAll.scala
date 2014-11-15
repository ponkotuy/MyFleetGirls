package models.join

import models.db._
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/10/12.
 */
case class MasterShipAll(
    base: MasterShipBase,
    specs: MasterShipSpecs,
    after: MasterShipAfter,
    other: MasterShipOther,
    stype: MasterStype) {
  import models.join.MasterShipAll._
  def id = base.id
  def name = base.name
  def stName = stype.name
  def yomi = base.yomi

  def hp = specs.hp
  def karyoku = Array(specs.karyokuMin, specs.soukoMax)
  def raisou = Array(specs.raisouMin, specs.raisouMax)
  def taiku = Array(specs.taikuMin, specs.taikuMax)
  def souko = Array(specs.soukoMin, specs.soukoMax)
  def lucky = Array(specs.luckyMin, specs.luckyMax)
  def length = LenTable(specs.length)

  def afterfuel = if(after.afterfuel > 0) Some(after.afterfuel) else None
  def afterbull = if(after.afterbull > 0) Some(after.afterbull) else None

  def afterResource = {
    for {
      fuel <- afterfuel
      bull <- afterbull
    } yield Array(fuel, bull)
  }

  def afterLv: Option[Int] = if(after.afterlv > 0) Some(after.afterlv) else None
  lazy val afterShip: Option[MasterShipAll] =
    MasterShipBase.findAllInOneBy(sqls"ms.id = ${after.aftershipid}").headOption

  lazy val beforeShip: List[MasterShipAll] =
    MasterShipBase.findAllInOneBy(sqls"msa.aftershipid = ${base.id}")

  def fuel = other.fuelMax
  def bull = other.bullMax
  def broken = Array(other.brokenFuel, other.brokenAmmo, other.brokenSteel, other.brokenBauxite)
  def buildtime = other.buildtime
  def powerup = Array(other.powupFuel, other.powupAmmo, other.powupSteel, other.powupBauxite)
  def backs = other.backs
}

object MasterShipAll {
  val LenTable = Map(1 -> "短", 2 -> "中" , 3 -> "長" , 4 -> "超長")
}
