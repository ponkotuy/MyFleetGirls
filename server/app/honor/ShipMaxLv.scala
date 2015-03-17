package honor

import models.db.Ship

/**
 * @author ponkotuy
 * Date: 15/03/18.
 */
object ShipMaxLv extends HonorCategory {
  override def category: Int = 5

  override def approved(memberId: Long): List[String] = {
    if(Ship.findAllByUser(memberId).exists(_.lv == 150)) "最強の一隻" :: Nil else Nil
  }
}
