package ranking

import models.db.{CalcScore, Ranking => dbRanking}
import util.Ymdh
import org.joda.time.Interval
import scalikejdbc._
import com.github.nscala_time.time.Imports._



/**
  * Date: 2016/01/02
  * @author ponkotuy
  */
object ScoreRankingCommon {
  val cs = CalcScore.cs
  val r = dbRanking.r
  val ObservedDelay = 3.hours

  def scoresFromCalc(interval: Interval): Map[Long, Int] = {
    val scores = CalcScore.findAllBy(interval2YmdhSyntax(cs.yyyymmddhh, interval))
    scores.groupBy(_.memberId).mapValues(_.maxBy(_.created).sum)
  }

  def scoresFromObserved(interval: Interval): Map[Long, Int] = {
    if(interval.duration < ObservedDelay) return Map() // まだ先月分しか表示されてない筈なので空を返す
    val fixed = interval.withStart(interval.start + ObservedDelay) // 取得結果が3時間先月分なので3時間様子を見る
    val scores = dbRanking.findAllBy(interval2MillisSyntax(r.created, fixed))
    scores.groupBy(_.memberId).mapValues(_.maxBy(_.created).rate)
  }

  private def interval2YmdhSyntax(column: SQLSyntax, interval: Interval): SQLSyntax = {
    sqls.ge(column, Ymdh.fromDate(interval.start).toInt)
        .and.le(column, Ymdh.fromDate(interval.end).toInt)
  }
  private def interval2MillisSyntax(column: SQLSyntax, interval: Interval): SQLSyntax =
    sqls.ge(column, interval.startMillis).and.le(column, interval.endMillis)
}
