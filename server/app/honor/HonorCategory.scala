package honor

/**
 * @author ponkotuy
 * Date: 15/03/17.
 */
trait HonorCategory {
  def category: Int
  def approved(memberId: Long): List[String]
}
