package honor

/**
 *
 * @author ponkotuy
 * Date: 15/03/22.
 */
object Rookie extends HonorCategory {
  override def category: Int = 12

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    db.basic.filter(_.lv <= 50).map { _ =>
      "駆け出し提督"
    }.toList
  }

  override def comment: String = "Lv50以下"
}
