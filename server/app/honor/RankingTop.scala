package honor

import models.db.Admiral
import ranking.common.{Ranking, RankingElement, RankingType}

import scala.collection.breakOut

/**
 *
 * @author ponkotuy
 * Date: 15/03/17.
 */
object RankingTop extends HonorCategory {
  override def category: Int = 3

  override def approved(memberId: Long): List[String] = {
    val rankings: Map[Ranking, Seq[RankingElement]] =
      RankingType.Admiral.rankings.map(it => it -> it.rankingQuery(20))(breakOut)
    val admiralName = Admiral.find(memberId).map(_.nickname)
    val tops = rankings.filter { case (admiral, xs) =>
      val top = xs.head.num
      xs.takeWhile(_.num == top).exists(admiralName.contains)
    }.keys
    tops.map(toHonor)(breakOut)
  }

  private def toHonor(ranking: Ranking): String = s"${ranking.title}1位"
}
