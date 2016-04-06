package modules


import javax.inject.{Inject, Singleton}

import akka.actor.{ActorSystem, Props}
import com.github.nscala_time.time.StaticDateTime
import com.google.inject.AbstractModule
import models.db._
import org.joda.time.{DateTimeConstants, LocalDate}
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import play.api._
import ranking.common.RankingType
import scalikejdbc._
import tool.BattleScore
import util.{Cron, CronSchedule, CronScheduler, Ymdh}

import scala.concurrent.duration._

/**
  *
  * PlayInitializerのDI依存は起動順序制御のため
  *
  * @author ponkotuy
  * Date: 14/05/12.
  */
@Singleton
class MFGCron @Inject()(val system: ActorSystem, _playInitializer: PlayInitializer) {
  import util.Cron._
  import util.MFGDateUtil._
  import system.dispatcher

  onStart()

  def onStart(): Unit = {
    beforeStart()
    val cron = system.actorOf(Props[CronScheduler], "cron")
    cron ! CronSchedule(Cron(0, 5, aster, aster, aster), _ => deleteDailyQuest())
    cron ! CronSchedule(Cron(0, 5, aster, aster, DateTimeConstants.MONDAY), _ => deleteWeeklyQuest())
    cron ! CronSchedule(Cron(0, 5, 1, aster, aster), _ => deleteMonthlyQuest())
    cron ! CronSchedule(Cron(17, 3, aster, aster, aster), _ => cutMaterialRecord())
    cron ! CronSchedule(Cron(23, 3, aster, aster, aster), _ => cutBasicRecord())
    cron ! CronSchedule(Cron(0, 2, aster, aster, aster), insertCalcScore)
    cron ! CronSchedule(Cron(0, 14, aster, aster, aster), insertCalcScore)
    cron ! CronSchedule(Cron(0, aster, aster, aster, aster), _ => insertRanking(Ymdh.now(Tokyo)))
    // 月末にだけやりたいのでとりあえず起動して内部でチェック
    cron ! CronSchedule(Cron(0, 22, aster, aster, aster), insertCalcScoreMonthly)
    system.scheduler.schedule(0.seconds, 45.seconds, cron, "minutes")
  }

  private def beforeStart(): Unit = {
    initRanking()
  }

  private def initRanking(): Unit = {
    val now = Ymdh.now(Tokyo)
    val mr = MyfleetRanking.mr
    if(MyfleetRanking.countBy(sqls.eq(mr.yyyymmddhh, now.toInt)) == 0L) insertRanking(now)
  }

  private def deleteDailyQuest(): Unit = {
    Logger.info("Delete Daily Quest")
    Quest.deleteAllBy(sqls"typ in(2, 4, 5)")
  }

  private def deleteWeeklyQuest(): Unit = {
    Logger.info("Delete Weekly Quest")
    Quest.deleteAllBy(sqls"typ = 3")
    Logger.info("Delete AGOProgress")
    AGOProgress.deleteAll()
  }

  private def deleteMonthlyQuest(): Unit = {
    Logger.info("Delete Monthly Quest")
    Quest.deleteAllBy(sqls"typ = 6")
  }

  private def cutMaterialRecord(): Unit = {
    Logger.info("Cut Material Record")
    Admiral.findAll().foreach { user =>
      val monthAgo = System.currentTimeMillis() - 30.days.toMillis
      val materials = Material.findAllByUser(user.id, to = monthAgo)
      val days = materials.groupBy { mat =>
        new LocalDate(mat.created, Tokyo)
      }.values
      days.foreach { daily =>
        val rest = Set(daily.minBy(_.steel).id, daily.maxBy(_.steel).id)
        daily.filterNot(mat => rest.contains(mat.id)).foreach(_.destroy())
      }
    }
  }

  private def cutBasicRecord(): Unit = {
    Logger.info("Cut Basic Record")
    Admiral.findAll().foreach { user =>
      val monthAgo = System.currentTimeMillis() - 30.days.toMillis
      val basics = Basic.findAllByUser(user.id, to = monthAgo)
      val days = basics.groupBy { b =>
        new LocalDate(b.created, Tokyo)
      }.values
      days.foreach { daily =>
        val rest = Set(daily.minBy(_.experience).id, daily.maxBy(_.experience).id)
        daily.filterNot(b => rest.contains(b.id)).foreach(_.destroy())
      }
    }
  }

  private def insertCalcScore(cron: Cron): Unit = {
    val now = StaticDateTime.now()
    val scores = Admiral.findAllIds().map { memberId =>
      val score = BattleScore.calcFromMemberId(memberId)
      val yyyymmddhh = Ymdh(now.getYear, now.getMonthOfYear, cron.day, cron.hour).toInt
      score.toCalcScore(memberId, yyyymmddhh, now.getMillis)
    }
    CalcScore.batchInsert(scores)
  }

  private def insertRanking(now: Ymdh): Unit = {
    implicit val formats = Serialization.formats(NoTypeHints)
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

  private def insertCalcScoreMonthly(cron: Cron): Unit = {
    val year = StaticDateTime.now().getYear
    if(cron.isEndOfMonth(year)) insertCalcScore(cron)
  }
}

class MFGCronModule extends AbstractModule {
  override def configure(): Unit = bind(classOf[MFGCron]).asEagerSingleton()
}
