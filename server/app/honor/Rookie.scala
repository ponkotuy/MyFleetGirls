package honor

import models.db.Basic

/**
 *
 * @author ponkotuy
 * Date: 15/03/22.
 */
object Rookie extends HonorCategory {
  override def category: Int = 12

  override def approved(memberId: Long): List[String] = {
    Basic.findByUser(memberId).filter(_.lv <= 50).map { _ =>
      "駆け出し提督"
    }.toList
  }

  override def comment: String = "Lv50以下"
}
