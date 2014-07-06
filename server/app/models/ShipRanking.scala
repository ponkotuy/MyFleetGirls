package models

import scalikejdbc._

/**
 *
 * Date: 14/07/07.
 */
object ShipRanking {
  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")

  def findAllOrderByExpSum(limit: Int = 10)(
      implicit session: DBSession = Ship.autoSession): List[(MasterShipBase, Long)] = {
    withSQL {
      select(ms.resultAll, sqls"sum(s.exp) as sum").from(Ship as s)
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .groupBy(s.shipId)
        .orderBy(sqls"sum").desc
        .limit(limit)
    }.map { rs =>
      MasterShipBase.apply(ms)(rs) -> rs.long(sqls"sum")
    }.list().apply()
  }
}
