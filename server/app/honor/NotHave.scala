package honor

import models.db.{MasterShipBase, Ship}
import ranking.EvolutionBase
import scalikejdbc._

import scala.collection.breakOut

/**
 * @author ponkotuy
 * Date: 15/04/16.
 */
case object NotHave extends HonorCategory {
  override def category: Int = 16

  override def comment: String = "持たざるものには分かる"

  override def approved(memberId: Long): List[String] = {
    val ships = Ship.findAllByUser(memberId)
    val haves: Set[Int] = ships.map { s => EvolutionBase(s.shipId) }(breakOut)
    MasterShipBase.findAllBy(sqls.in(MasterShipBase.ms.id, (Target -- haves).toSeq)).map { ship =>
      s"${ship.name}出ない"
    }
  }

  val Target = Set(184, 131, 143, 153, 161, 171)
}
