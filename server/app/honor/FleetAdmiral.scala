package honor

import models.db.Basic

/**
 *
 * @author ponkotuy
 * Date: 15/03/18
 */
object FleetAdmiral extends HonorCategory {
  override def category: Int = 6

  override def approved(memberId: Long): List[String] = {
    if(Basic.findByUser(memberId).exists(_.rank == 1)) "元帥" :: Nil else Nil
  }
}
