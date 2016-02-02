package honor

import models.db.MasterShipBase
import ranking.common.EvolutionBase
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/03/18.
 */
object Married extends HonorCategory {
  override def category: Int = 10

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val marrieds = db.shipWithName.filter(_.lv >= 100).map(_.shipId)
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

  override val comment: String = "ケッコン艦が0、1、2以上、10以上でそれぞれ獲得。ケッコン艦が2以上なおかつそれが同種である場合に別の称号"
}
