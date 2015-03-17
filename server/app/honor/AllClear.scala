package honor

import models.db.MapInfo
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 15/03/18.
 */
object AllClear extends HonorCategory {
  override def category: Int = 4

  override def approved(memberId: Long): List[String] = {
    val info = MapInfo.findAllBy(sqls.eq(MapInfo.column.memberId, memberId))
    if(info.forall(_.cleared) && info.nonEmpty) "全クリア" :: Nil else Nil
  }
}
