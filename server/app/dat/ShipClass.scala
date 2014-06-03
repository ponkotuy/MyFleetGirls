package dat

import scalikejdbc._
import models.MasterShipBase

/**
 * @author ponkotuy
 * Date: 14/04/10
 */
case class ShipClass(ctype: Int, name: String)

object ShipClass {
  def apply(ms: SyntaxProvider[MasterShipBase])(rs: WrappedResultSet): Option[ShipClass] = {
    for {
      ctype <- rs.intOpt(ms.ctype)
      name <- rs.stringOpt(ms.name)
    } yield ShipClass(ctype, name + "型")
  }

  def getOneClass(ms: SyntaxProvider[MasterShipBase])(rs: WrappedResultSet): ShipClass = {
    val ctype = rs.int(ms.ctype)
    val name = rs.string(ms.name)
    ShipClass(ctype, name)
  }
}
