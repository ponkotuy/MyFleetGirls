package honor

import models.db.{MinimumRanking, MyfleetRanking}
import ranking.common.RankingType
import scalikejdbc._

import scala.collection.breakOut

/**
 *
 * @author ponkotuy
 * Date: 15/03/17.
 */
object RankingTop extends HonorCategory {
  override def category: Int = 3

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val rankings = findRankings(memberId)
    val tops = rankings.filter(_.rank == 1)
    val topHonors = tops.flatMap { top => top.ranking.map { t => s"${t.title}トップ" } }.distinct
    val inHonors = rankings.flatMap { in => in.ranking.map { i => s"${i.title}ランクイン"} }.distinct
    topHonors ++ inHonors
  }

  override val comment: String = "ランキングトップとランクイン"
  def findRankings(memberId: Long)(implicit session: DBSession = AutoSession): List[MinimumRanking] = {
    val rankingIds: Set[Int] = RankingType.Admiral.rankings.map(_.id)(breakOut)
    def mr = MyfleetRanking.mr
    val where = sqls.eq(mr.targetId, memberId).and.le(mr.rank, 20).and.in(mr.rankingId, rankingIds.toSeq)
    MyfleetRanking.findAllByMininum(where)
  }
}
