package honor

import models.db.Ship
import scalikejdbc._

/**
 * 一定Lv超えた艦が一定数いたら
 *
 * @author ponkotuy
 * Date: 15/03/22.
 */
object ManyShip extends HonorCategory {
  override def category: Int = 11

  override def approved(memberId: Long): List[String] = {
    val s = Ship.s
    val count = Ship.countBy(sqls.eq(s.memberId, memberId).and.ge(s.lv, 50))
    if(50 <= count) "精鋭の大艦隊" :: Nil else Nil
  }
}
