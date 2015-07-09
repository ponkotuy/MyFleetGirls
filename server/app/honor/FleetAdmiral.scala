package honor

/**
 *
 * @author ponkotuy
 * Date: 15/03/18
 */
object FleetAdmiral extends HonorCategory {
  override def category: Int = 6

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    if(db.basic.exists(_.rank == 1)) "元帥" :: Nil else Nil
  }

  override val comment: String = "元帥になる"
}
