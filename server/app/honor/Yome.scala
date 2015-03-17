package honor

import models.db
import models.db.{MasterShipBase, Ship}
import ranking.EvolutionBase
import scalikejdbc._

import scala.collection.breakOut

/**
 *
 * @author ponkotuy
 * Date: 15/03/17.
 */
object Yome extends HonorCategory {
  override def category: Int = 2

  override def approved(memberId: Long): List[String] = {
    val yomes: Set[Int] = db.YomeShip.findAllBy(sqls.eq(db.YomeShip.column.memberId, memberId))
        .map { s => EvolutionBase(s.shipId) }(breakOut)
    val married: Set[Int] = Ship.findAllByUser(memberId)
      .filter(100 <= _.lv)
      .map { s => EvolutionBase(s.shipId) }(breakOut)
    val ids = yomes & married
    val result = MasterShipBase.findAllBy(sqls.in(MasterShipBase.column.id, ids.toSeq))
    result.map(toHonor)
  }

  private def toHonor(ms: MasterShipBase): String = s"${ms.name}は嫁"
}
