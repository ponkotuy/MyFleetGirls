package honor

import models.db.MasterShipBase
import ranking.common.EvolutionBase
import scalikejdbc._

import scala.collection.breakOut
import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 15/03/17.
 */
object Yome extends HonorCategory {
  override def category: Int = 2

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val yomes: Set[Int] = db.yomeShip
        .map { s => EvolutionBase(s.shipId) }(breakOut)
    val married: Set[Int] = db.shipWithName
        .filter(100 <= _.lv)
        .map { s => EvolutionBase(s.shipId) }(breakOut)
    val maxLv = Try(db.shipWithName.maxBy(_.lv)).map(s => EvolutionBase(s.shipId)).toOption
    val ids = yomes & (married ++ maxLv)
    val withAlias: Seq[Int] = ids.toSeq ++ ids.flatMap(EvolutionBase.Aliases.get)(breakOut)
    val result = MasterShipBase.findAllBy(sqls.in(MasterShipBase.column.id, withAlias))
    result.map(toHonor)
  }

  private def toHonor(ms: MasterShipBase): String = s"${ms.name}は嫁"

  override val comment: String = "Lvが100以上または所持艦の中でLvが最大で、なおかつ嫁設定済"
}
