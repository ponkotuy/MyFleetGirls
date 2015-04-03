package honor

import models.db.{MasterShipBase, Ship}
import ranking.EvolutionBase
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/03/27.
 */
object Lucky extends HonorCategory {
  override def category: Int = 14

  override def approved(memberId: Long): List[String] = {
    val luckyMax = Ship.findAllWithSpec(sqls.eq(Ship.s.memberId, memberId).and.eq(Ship.mss.luckyMax, Ship.s.lucky))
    val luckies = luckyMax.map { s => EvolutionBase(s.shipId) }
    val withAlias = luckies ++ luckies.flatMap(EvolutionBase.Aliases.get)
    val names = MasterShipBase.findAllBy(sqls.in(MasterShipBase.ms.id, withAlias))
    names.map("幸運の" + _.name).distinct ++
        (if(luckyMax.nonEmpty) Some("幸運の女神") else None)
  }

  override val comment: String = "運のステータスがMAXになる"
}
