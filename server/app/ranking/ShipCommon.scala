package ranking

import controllers.routes
import models.db.MasterShipBase
import ranking.common.{EvolutionBase, RankingElement}
import ranking.data.Count

import scala.collection.mutable

/**
 *
 * @author ponkotuy
 * Date: 15/02/27.
 */
object ShipCommon {
  /**
   *
   * 艦娘一覧のRankingElementを生成
 *
   * @param xs: (艦娘ID, count)
   */
  def toRankingElement(xs: Seq[(Int, Long)]): Seq[RankingElement] = {
    val result = aggregateCountToBase(xs).sortBy(-_._2)
    val masters: Map[Int, MasterShipBase] = MasterShipBase.findAll().map { ship => ship.id -> ship }(collection.breakOut)
    val withMasters = result.flatMap { case (id, count) => masters.get(id).map(_ -> count) }
    withMasters.map { case (master, count) =>
      val url = routes.ViewSta.shipBook(master.id).toString
      RankingElement(master.id, master.name, Count(count), url, count)
    }
  }

  /** 進化元に集約 */
  def aggregateCountToBase(xs: Seq[(Int, Long)]): Seq[(Int, Long)] = {
    val map = mutable.Map[Int, Long]().withDefaultValue(0L)
    xs.foreach { case (shipId, count) =>
      map(EvolutionBase(shipId)) += count
    }
    map.toSeq
  }
}

