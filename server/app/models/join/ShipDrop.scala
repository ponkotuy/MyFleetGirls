package models.join

import com.ponkotuy.data.GetShip
import models.db.{BattleResult, CellInfo}
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/04/23.
 */
case class ShipDrop(
    areaId: Int, infoNo: Int, cell: Int,
    getShipId: Option[Int], getShipType: Option[String], getShipName: Option[String]) {
  def stage: String = s"$areaId-$infoNo"
  def point: (Int, Int, Int) = (areaId, infoNo, cell)
  def pointStr: String = s"$areaId-$infoNo-$cell"

  def getShip: Option[GetShip] = for {
    id <- getShipId
    typ <- getShipType
    name <- getShipName
  } yield {
    GetShip(id, typ, name)
  }

  def cellInfo(): Option[CellInfo] = CellInfo.find(areaId, infoNo, cell)
}

object ShipDrop {
  def apply(br: SyntaxProvider[BattleResult])(rs: WrappedResultSet): ShipDrop = new ShipDrop(
    rs.int(br.areaId),
    rs.int(br.infoNo),
    rs.int(br.cell),
    rs.intOpt(br.getShipId),
    rs.stringOpt(br.getShipType),
    rs.stringOpt(br.getShipName)
  )
}
