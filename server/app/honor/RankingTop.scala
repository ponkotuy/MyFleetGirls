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
      xs.headOption.exists(top => xs.takeWhile(_.num == top.num).exists(x => admiralName.contains(x.name)))
    }.keys
    val ins = rankings.filter { case (_, xs) => xs.exists(x => admiralName.contains(x.name)) }.keys
    (tops.map(top => s"${top.title}トップ") ++ ins.map(in => s"${in.title}ランクイン")).toList
  }
}
