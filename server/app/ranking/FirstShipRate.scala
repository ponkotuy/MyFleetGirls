package ranking

import controllers.routes
import models.db
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/12/05.
 */
case object FirstShipRate extends Ranking {
  import ranking.Ranking._
  override val title: String = "初期艦"

  override def rankingQuery(limit: Int): List[RankingElement] = {
    val counts = db.Ship.countAllShip(sqls"s.id = 1")
      .map { case (sid, count) =>
        ExpByShipRanking.evolutionBase(sid) -> count
      }
    val masters = db.MasterShipBase.findAllBy(sqls"ms.id in (${counts.keySet})")
      .map { it => it.id -> it }.toMap
    val sum = counts.map(_._2).sum
    counts.map { case (sid, count) =>
      val master = masters(sid)
      val url = routes.ViewSta.shipBook(sid).toString()
      RankingElement(master.name, <span><strong>{f"$count%,d"}</strong>{s" / $sum"}</span>, url)
    }.toList
  }

  override def comment: List[String] = Nil

  override def divClass: String = colmd3
}
