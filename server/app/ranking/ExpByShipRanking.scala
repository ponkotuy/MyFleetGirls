package ranking

import scalikejdbc._
import models._
import scala.collection.mutable

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
object ExpByShipRanking extends Ranking {
  import Ranking._

  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")

  override val title: String = "艦娘別Exp"
  override val comment: List[String] = List("艦娘別Expは進化前で集計しています")
  override val divClass: String = colmd3

  override def rankingQuery(limit: Int): List[RankingElement] =
    findAllOrderByExpSum(limit).map { case (name, exp) => RankingElement(name, <span>{exp}</span>) }

  private def findAllOrderByExpSum(limit: Int = 10)(
      implicit session: DBSession = Ship.autoSession): List[(String, Long)] = {
    val result = withSQL {
      select(ms.resultAll, sqls"sum(s.exp) as sum").from(Ship as s)
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .groupBy(s.shipId)
        .orderBy(sqls"sum").desc
        .limit(limit)
    }.map { rs =>
      MasterShipBase.apply(ms)(rs) -> rs.long(sqls"sum")
    }.list().apply()

    // 進化元に集約
    val map = mutable.Map[Int, Long]().withDefaultValue(0L)
    result.foreach { case (master, count) =>
      map(evolutionBase(master.id)) += count
    }

    // 名前付与
    val masters = MasterShipBase.findAll()
      .map(ship => ship.id -> ship.name).toMap
    map.map { case (id, count) => masters(id) -> count }.toList.sortBy(_._2).reverse
  }

  private lazy val afters = MasterShipAfter.findAll()
    .filterNot(_.aftershipid == 0)
    .map(ship => ship.aftershipid -> ship.id).toMap

  private def evolutionBase(shipId: Int): Int = {
    afters.get(shipId) match {
      case Some(afterId) => evolutionBase(afterId)
      case None => shipId
    }
  }
}
