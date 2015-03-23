package models.view

import models.db.ShipBook
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/03/23.
 */
case class ShipBookCountByShip(id: Int, name: String, count: Long)

object ShipBookCountByShip {
  def apply(sb: ResultName[ShipBook])(rs: WrappedResultSet): ShipBookCountByShip = new ShipBookCountByShip(
    rs.get(sb.id),
    rs.get(sb.name),
    rs.get(sqls.count)
  )
}
