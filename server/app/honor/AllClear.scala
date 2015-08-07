package honor

/**
 *
 * @author ponkotuy
 * Date: 15/03/18.
 */
object AllClear extends HonorCategory {
  override def category: Int = 4

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val info = db.mapInfo
    if(info.forall(_.cleared) && info.nonEmpty) "全クリア" :: Nil else Nil
  }

  override val comment: String = "未クリア海域が無い状態にする"
}
