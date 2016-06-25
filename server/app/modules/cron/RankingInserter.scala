package modules.cron

import models.db.MyfleetRanking
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import play.api.Logger
import ranking.common.RankingType
import util.{Cron, CronExecutor, Ymdh}

object RankingInserter extends CronExecutor {
  import util.MFGDateUtil._

  implicit val formats = Serialization.formats(NoTypeHints)

  override def exec(cron: Cron): Unit = {
    val now = Ymdh.now(Tokyo)
    run(now)
  }

  def run(now: Ymdh): Unit = {
    Logger.info("Insert MyfleetRanking")
    RankingType.values.foreach { rType =>
      rType.rankings.foreach { rank =>
        val result = rank.rankingQuery(200)
        val rankings = result.zipWithIndex.map { case (r, idx) =>
          MyfleetRanking(0L, rank.id, idx + 1, now.toInt, r.targetId, r.name, write(r.data), r.url, r.num, System.currentTimeMillis())
        }
        if(rankings.nonEmpty) MyfleetRanking.batchInsert(rankings)
      }
    }
  }
}
