package honor

import models.db.Ship
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/03/18.
 */
object Married extends HonorCategory {
  override def category: Int = 10

  override def approved(memberId: Long): List[String] = {
    val married = Ship.countBy(sqls.eq(Ship.column.memberId, memberId).and.ge(Ship.column.lv, 100))
    List(
      if(married == 1) Some("一途") else None,
      if(married >= 2) Some("重婚カッコカリ") else None,
      if(married >= 10) Some("ハーレムカッコガチ") else None
    ).flatten
  }
}
