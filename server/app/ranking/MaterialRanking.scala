package ranking

import controllers.routes
import models.db.{Admiral, Material}
import scalikejdbc._
import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
case object MaterialRanking extends Ranking {
  import ranking.Ranking._

  def a = Admiral.a
  lazy val m = Material.syntax("m")

  override val title: String = "資源保有量"

  override val divClass: String = collg3

  override val comment: List[String] = List(comment7days)

  override def rankingQuery(limit: Int): List[RankingElement] = {
    findAllOrderByMaterial(limit, agoMillis(7.days)).map { case (admiral, count) =>
      val url = routes.UserView.material(admiral.id).toString()
      RankingElement(admiral.nickname, <span>{f"$count%,d"}</span>, url)
    }
  }

  private def findAllOrderByMaterial(limit: Int = 10, from: Long = 0L)(
    implicit session: DBSession = Admiral.autoSession): List[(Admiral, Int)] = {
    withSQL {
      select.from(Admiral as a)
        .innerJoin(Material as m).on(a.id, m.memberId)
        .where.eq(m.created, sqls"(select MAX(${m.created}) from ${Material.table} as m where ${a.id} = ${m.memberId})")
        .and.gt(m.created, from)
        .orderBy(sqls"(${m.fuel} + ${m.ammo} + ${m.steel} + ${m.bauxite})").desc
        .limit(limit)
    }.map { rs =>
      val mat = Material(m)(rs)
      Admiral(a)(rs) -> (mat.fuel + mat.ammo + mat.steel + mat.bauxite)
    }.list().apply()
  }
}
