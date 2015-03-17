package honor

import models.db.Ship
import tool.STypeExp

/**
 *
 * @author ponkotuy
 * Date: 15/03/18.
 */
object ShipTypeBias extends HonorCategory {
  override def category: Int = 8

  override def approved(memberId: Long): List[String] = {
    val ships = Ship.findAllByUserWithName(memberId)
    val result = STypeExp.fromShips(ships).maxBy(_.exp)
    s"${result.name}提督" :: (if(result.name == "駆逐艦") "ロリコン提督" :: Nil else Nil)
  }
}
