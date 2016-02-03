package ranking

import controllers.routes
import models.db
import org.json4s._
import ranking.common._
import ranking.data.CountWithSum
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/12/05.
 */
case object FirstShipRate extends Ranking {
  import ranking.common.Ranking._

  override val id = 104
  override val title: String = "初期艦"
  override val comment: List[String] = List("進化前で集計しています", "初期艦大井は事前登録特典です")
  override val divClass: String = colmd3

  override def rankingQuery(limit: Int): List[RankingElement] = {
    val counts = db.Ship.countAllShip(sqls"s.id = 1")
      .groupBy(t => EvolutionBase(t._1))
      .mapValues(_.values.sum)
    val masters = db.MasterShipBase.findAllBy(sqls.in(db.MasterShipBase.ms.id, counts.keys.toSeq))
      .map { it => it.id -> it }.toMap
    val sum = counts.values.sum
    counts.toList.sortBy(-_._2).map { case (sid, count) =>
      val master = masters(sid)
      val url = routes.ViewSta.shipBook(sid).toString
      RankingElement(master.id, master.name, CountWithSum(count, sum), url, count)
    }
  }

  override def decodeData(v: JValue): Option[RankingData] = CountWithSum.decode(v)
}
