package honor

import models.db.{MasterShipBase, Ship}
import ranking.EvolutionBase
import scalikejdbc._

import scala.collection.mutable

/**
 *
 * @author ponkotuy
 * Date: 15/03/17.
 */
object ShipMaster extends HonorCategory {
  override def category = 1

  override def approved(memberId: Long): List[String] = {
    val ships = Ship.findAllByUser(memberId)
    val lvs = mutable.Map[Int, Int]().withDefaultValue(0)
    ships.foreach { ship =>
      lvs(EvolutionBase(ship.shipId)) += ship.lv
    }
    val result = lvs.toVector.sortBy(-_._2).takeWhile(_._2 >= 200).map(_._1)
    val withAliases = result ++ result.flatMap(EvolutionBase.Aliases.get)
    val mss = MasterShipBase.findAllBy(sqls.in(MasterShipBase.column.id, withAliases))
    mss.flatMap { ms => toHonor(ms) :: OriginalHonor.get(ms.id).toList }
  }

  private def toHonor(ms: MasterShipBase): String = s"${ms.name}提督"

  val OriginalHonor = Map(
    71 -> "我輩は利根提督である",
    45 -> "夕立提督っぽい",
    35 -> "ハラショー",
    56 -> "アイドル提督",
    51 -> "俺の名は天龍提督",
    54 -> "夜戦提督",
    125 -> "お嬢様提督",
    15 -> "クソ提督"
  )

  override val comment: String = "ある艦娘（改造後含む）の合計Lvが200以上。特定艦娘固有称号含む"
}
