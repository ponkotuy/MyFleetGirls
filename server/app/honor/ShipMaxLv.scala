package honor

/**
 * @author ponkotuy
 * Date: 15/03/18.
 */
object ShipMaxLv extends HonorCategory {
  override def category: Int = 5

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    if(db.shipWithName.exists(_.lv == 155)) "最強の一隻" :: Nil else Nil
  }

  override val comment: String = "艦娘をLv155にする"
}
