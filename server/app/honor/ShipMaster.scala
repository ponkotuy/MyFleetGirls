package honor

import models.db.{MasterShipBase, Ship}
import ranking.EvolutionBase
import scalikejdbc._

import scala.collection.mutable

/**
 *
 * @author ponkotuy
 * Date: 15/03/17.
 */
object ShipMaster extends HonorCategory {
  override def category = 1

  override def approved(memberId: Long): List[String] = {
    val ships = Ship.findAllByUserWithName(memberId)
    val lvs = mutable.Map[Int, Int]().withDefaultValue(0)
    ships.foreach { ship =>
      lvs(EvolutionBase(ship.shipId)) += ship.lv
    }
    val result = lvs.toVector.sortBy(-_._2).takeWhile(_._2 >= 200).map(_._1).take(5)
    val mss = MasterShipBase.findAllBy(sqls.in(MasterShipBase.column.id, result))
    mss.map(toHonor)
  }

  private def toHonor(ms: MasterShipBase): String = s"${ms.name}提督"
}
