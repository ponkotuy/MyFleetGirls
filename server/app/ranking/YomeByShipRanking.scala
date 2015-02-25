package ranking

import controllers.routes
import models.db.{MasterShipBase, YomeShip}
import ranking.common.{Ranking, RankingElement}

/**
 *
 * @author ponkotuy
 * Date: 15/02/25.
 */
case object YomeByShipRanking extends Ranking {
  import ranking.common.Ranking._

  override val title = "艦娘別の嫁数"

  override val comment = List(s"${title}は進化前で集計しています")

  override val divClass = colmd3

  override def rankingQuery(limit: Int): Vector[RankingElement] = {
    countAllByShip().take(limit).map { case (ship, count) =>
      val url = routes.ViewSta.shipBook(ship.id).toString()
      RankingElement(ship.name, <span>{count}</span>, url)
    }
  }

  private def countAllByShip(): Vector[(MasterShipBase, Long)] = {
    val result = YomeShip.countAllByShip()

    // 進化元に集約
    val map = ExpByShipRanking.aggregateCountToBase(result.map { case (ship, count) => ship.id -> count })

    // 名前付与
    val masters: Map[Int, MasterShipBase] = MasterShipBase.findAll().map(ship => ship.id -> ship)(collection.breakOut)
    val ranking: Vector[(MasterShipBase, Long)] = map.map { case (id, count) => masters(id) -> count }(collection.breakOut)
    ranking.sortBy(-_._2).takeWhile(_._2 > 1)
  }
}
