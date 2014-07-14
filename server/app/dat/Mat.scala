package dat

import scalikejdbc._
import models._

/**
 * Date: 14/07/15.
 */
case class Mat(fuel: Int, ammo: Int, steel: Int, bauxite: Int, develop: Int)

object Mat {
  def apply(cs: SyntaxProvider[CreateShip])(rs: WrappedResultSet): Mat =
    new Mat(
      rs.int(cs.fuel),
      rs.int(cs.ammo),
      rs.int(cs.steel),
      rs.int(cs.bauxite),
      rs.int(cs.develop)
    )
}
