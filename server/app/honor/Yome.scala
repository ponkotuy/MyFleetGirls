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
    val yomes: Set[Int] = db.YomeShip.findAllByWithName(sqls.eq(db.YomeShip.ys.memberId, memberId))
        .map { s => EvolutionBase(s.shipId) }(breakOut)
    val married: Set[Int] = Ship.findAllByUser(memberId)
        .filter(100 <= _.lv)
        .map { s => EvolutionBase(s.shipId) }(breakOut)
    val maxLv = Ship.findByUserMaxLvWithName(memberId).map(s => EvolutionBase(s.shipId))
    val ids = yomes & (married ++ maxLv)
    val withAlias: Seq[Int] = ids.toSeq ++ ids.flatMap(EvolutionBase.Aliases.get)(breakOut)
    val result = MasterShipBase.findAllBy(sqls.in(MasterShipBase.column.id, withAlias))
    result.map(toHonor)
  }

  private def toHonor(ms: MasterShipBase): String = s"${ms.name}は嫁"

  override val comment: String = "Lvが100以上または所持艦の中でLvが最大で、なおかつ嫁設定済"
}
