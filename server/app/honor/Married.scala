package honor

import models.db.{MasterShipBase, Ship}
import ranking.EvolutionBase
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/03/18.
 */
object Married extends HonorCategory {
  override def category: Int = 10

  override def approved(memberId: Long): List[String] = {
    val marrieds = Ship.findAllBy(sqls.eq(Ship.column.memberId, memberId).and.ge(Ship.column.lv, 100)).map(_.shipId)
    val married = marrieds.size
    val marriedDist = marrieds.map(EvolutionBase(_)).distinct
    val marriedOne = {
      if(marriedDist.size == 1) {
        val withAliases = marriedDist ++ marriedDist.flatMap(EvolutionBase.Aliases.get)
        MasterShipBase.findAllBy(sqls.in(MasterShipBase.ms.id, withAliases))
      } else Nil
    }
    List(
      if(married == 0) Some("独身カッコカリ") else None,
      if(marriedDist.size == 1) Some("一途") else None,
      if(married >= 2) Some("重婚カッコカリ") else None,
      if(married >= 10) Some("ハーレムカッコガチ") else None
    ).flatten ++
        marriedOne.map(s => s"${s.name}に一途") ++
        (if(marriedDist.size == 1 && married >= 2) marriedOne.map(s => s"${s.name}の園") else Nil)
  }
}
