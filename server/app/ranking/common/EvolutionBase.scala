package ranking.common

import com.ponkotuy.value.ShipIds
import models.db.MasterShipAfter
import util.PeriodicalCache

import scala.concurrent.duration._

/**
 * Thread Safe
 *
 * @author ponkotuy
 * Date: 15/01/10.
 */
object EvolutionBase {
  import ShipIds._

  def apply(shipId: Int, count: Int = 10): Int = {
    if(count == 0) return shipId
    Afters.get(shipId) match {
      case Some(afterId) => apply(afterId, count - 1)
      case None => shipId
    }
  }

  def isBase(shipId: Int): Boolean = Afters.get(shipId).isEmpty

  // 大鯨→龍鳳、U-511→呂500など名称変更が入る艦娘のid mapping
  val Aliases = Map(Taigei -> Ryuho, Hibiki -> Vernyj, U511 -> Ro500, Littorio -> Italia)

  // 艦これの仕様に循環で進化する艦娘が導入された。このとき、進化元は2つあることがある
  // そこで一旦groupByでそのリストを生成し、小さいidを正しい、循環しない進化元であると仮定する
  private[this] def createCache(): Map[Int, Int] =
    MasterShipAfter.findAll()
        .filterNot(_.aftershipid == 0)
        .map { ship => ship.aftershipid -> ship.id }
        .groupBy(_._1)
        .mapValues { xs => xs.map(_._2).min }

  val Afters = new PeriodicalCache(60.minutes, createCache)
}
