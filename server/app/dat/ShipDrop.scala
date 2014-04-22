package dat

import scalikejdbc.WrappedResultSet
import scalikejdbc.SQLInterpolation._
import models.BattleResult
import com.ponkotuy.data.GetShip

/**
 *
 * @author ponkotuy
 * Date: 14/04/23.
 */
case class ShipDrop(
    areaId: Int, infoNo: Int, cell: Int,
    getShipId: Option[Int], getShipType: Option[String], getShipName: Option[String]) {
  def stage: String = s"$areaId-$infoNo"
  def point: String = s"$stage-$cell"

  def getShip: Option[GetShip] = for {
    id <- getShipId
    typ <- getShipType
    name <- getShipName
  } yield {
    GetShip(id, typ, name)
  }
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
