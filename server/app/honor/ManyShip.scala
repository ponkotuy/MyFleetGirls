package honor

/**
 * 一定Lv超えた艦が一定数いたら
 *
 * @author ponkotuy
 * Date: 15/03/22.
 */
object ManyShip extends HonorCategory {
  override def category: Int = 11

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val count = db.shipWithName.count(_.lv >= 50)
    if(50 <= count) "精鋭の大艦隊" :: Nil else Nil
  }

  override def comment: String = "Lv50以上の艦が50隻以上"
}
