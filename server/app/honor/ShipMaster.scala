package honor

import models.db.MasterShipBase
import ranking.common.EvolutionBase
import scalikejdbc._

import scala.collection.mutable

/**
 *
 * @author ponkotuy
 * Date: 15/03/17.
 */
object ShipMaster extends HonorCategory {
  import com.ponkotuy.value.ShipIds._
  override def category = 1

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val ships = db.shipWithName
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
    Tone -> "我輩は利根提督である",
    Yudachi -> "夕立提督っぽい",
    Hibiki -> "ハラショー",
    Naka -> "アイドル提督",
    Tenryu -> "俺の名は天龍提督",
    Sendai -> "夜戦提督",
    Kumano -> "お嬢様提督",
    Akebono -> "クソ提督",
    Sazanami -> "ご主人様"
  )

  override val comment: String = "ある艦娘（改造後含む）の合計Lvが200以上。特定艦娘固有称号含む"
}
