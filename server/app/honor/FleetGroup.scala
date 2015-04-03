package honor

import models.db
import models.join.FleetGroupWithShip
import ranking.EvolutionBase
import collection.breakOut

/**
 *
 * @author ponkotuy
 * Date: 15/03/22.
 */
object FleetGroup extends HonorCategory {
  override def category: Int = 13

  override def approved(memberId: Long): List[String] = {
    val fgs = FleetGroupWithShip.findAll()
    val shipIds: Set[Int] = db.Ship.findAllByUser(memberId).map(_.shipId).map(EvolutionBase(_))(breakOut)
    val groups = fgs.filter(_.ships.forall(shipIds.contains))
    groups.map(_.group.name) ++ groups.flatMap { g => OriginalHonor.get(g.group.id) }
  }

  val OriginalHonor = Map(1L -> "鎮守府の朝チュン")

  override val comment: String = "特定の艦娘をそれぞれ所持する"
}
