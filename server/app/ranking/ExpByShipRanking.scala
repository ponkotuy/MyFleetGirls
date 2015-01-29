package ranking

import controllers.routes
import models.db._
import ranking.common.{Ranking, RankingElement}
import scalikejdbc._

import scala.collection.mutable

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
case object ExpByShipRanking extends Ranking {
  import ranking.common.Ranking._

  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")

  override val title: String = "艦娘別Exp"
  override val comment: List[String] = List("艦娘別Expは進化前で集計しています")
  override val divClass: String = colmd3

  override def rankingQuery(limit: Int): List[RankingElement] =
    findAllOrderByExpSum(limit).map { case (id, name, exp) =>
      val url = routes.ViewSta.shipBook(id).toString()
      RankingElement(name, <span>{f"$exp%,d"}</span>, url)
    }

  private def findAllOrderByExpSum(limit: Int = 10)(
      implicit session: DBSession = Ship.autoSession): List[(Int, String, Long)] = {
    val result = withSQL {
      select(ms.resultAll, sqls"sum(s.exp) as sum").from(Ship as s)
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .groupBy(s.shipId)
        .orderBy(sqls"sum").desc
    }.map { rs =>
      MasterShipBase.apply(ms)(rs) -> rs.long(sqls"sum")
    }.list().apply()

    // 進化元に集約
    val map = mutable.Map[Int, Long]().withDefaultValue(0L)
    result.foreach { case (master, count) =>
      map(EvolutionBase(master.id)) += count
    }

    // 名前付与
    val masters = MasterShipBase.findAll()
      .map(ship => ship.id -> ship.name).toMap
    map.map { case (id, count) => (id, masters(id), count) }.toList.sortBy(_._3).reverse.take(limit)
  }
}
