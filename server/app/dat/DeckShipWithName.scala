package dat

import dat.ShipParameter.RGB
import scalikejdbc._
import models._

/**
 *
 * @author ponkotuy
 * Date: 14/04/15.
 */
case class DeckShipWithName(deckId: Int, num: Int, memberId: Long, shipId: Int, lv: Int, cond: Int, name: String) {
  def rgb: RGB = ShipParameter.rgb(cond)
}

object DeckShipWithName {
  def apply(ds: SyntaxProvider[DeckShip], s: SyntaxProvider[Ship], ms: SyntaxProvider[MasterShipBase])(
      rs: WrappedResultSet): DeckShipWithName =
    new DeckShipWithName(
      rs.int(ds.deckId),
      rs.int(ds.num),
      rs.long(ds.memberId),
      rs.int(ds.shipId),
      rs.int(s.lv),
      rs.int(s.cond),
      rs.string(ms.name)
    )
}
