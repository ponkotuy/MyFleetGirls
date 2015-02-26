package ranking

import controllers.routes
import models.db
import ranking.common.{Ranking, RankingElement}
import scalikejdbc._

import scala.collection.mutable

/**
 *
 * @author ponkotuy
 * Date: 14/12/05.
 */
case object FirstShipRate extends Ranking {
  import ranking.common.Ranking._
  override val title: String = "初期艦"

  override def rankingQuery(limit: Int): List[RankingElement] = {
    val counts = mutable.Map[Int, Long]().withDefaultValue(0)
    db.Ship.countAllShip(sqls"s.id = 1").foreach { case (sid, count) =>
      counts(EvolutionBase(sid)) += count
    }
    val masters = db.MasterShipBase.findAllBy(sqls"ms.id in (${counts.keys.toSet})")
      .map { it => it.id -> it }.toMap
    val sum = counts.values.sum
    counts.toList.sortBy(-_._2).map { case (sid, count) =>
      val master = masters(sid)
      val url = routes.ViewSta.shipBook(sid).toString()
      RankingElement(master.name, <span><strong>{f"$count%,d"}</strong>{s" / $sum"}</span>, url)
    }.toList
  }

  override def comment: List[String] = List("進化前で集計しています")

  override def divClass: String = colmd3
}
