package honor

import models.db.MasterShipBase
import ranking.EvolutionBase

/**
 *
 * @author ponkotuy
 * Date: 15/03/18
 */
object InitShip extends HonorCategory {
  override def category: Int = 9

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val result = for {
      ship <- db.shipWithName.find(_.id == 1)
      base = EvolutionBase(ship.shipId)
      ms <- MasterShipBase.find(base)
    } yield {
      s"初期艦${ms.name}" :: (if(ship.lv == 150) s"ずっと${ms.name}と一緒" :: Nil else Nil)
    }
    result.getOrElse(Nil)
  }

  override val comment: String = "選んだ初期艦の称号が手に入る。初期艦Lv150で追加称号"
}
