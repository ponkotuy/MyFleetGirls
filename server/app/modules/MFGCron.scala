package modules


import javax.inject.{Inject, Singleton}

import akka.actor.{ActorSystem, InvalidActorNameException}
import com.google.inject.AbstractModule
import models.db._
import modules.cron._
import org.joda.time.DateTimeConstants
import play.api._
import scalikejdbc._
import util.{CronScheduler, Ymdh}

import scala.concurrent.ExecutionContext


/**
  *
  * PlayInitializerのDI依存は起動順序制御のため
  *
  * @author ponkotuy
  * Date: 14/05/12.
  */
@Singleton
class MFGCron @Inject()(val system: ActorSystem, _playInitializer: PlayInitializer, implicit val ec: ExecutionContext) {
  import util.Cron._
  import util.MFGDateUtil._

  onStart()

  def onStart(): Unit = {
    Logger.info("Start MFGCron")
    beforeStart()
    val cron = try {
      CronScheduler.create(system, "cron")
    } catch {
      case _: InvalidActorNameException =>
        Logger.warn("Raised InvalidActorNameException. Not do anything this MFGCron.")
        return
    }
    cron ! DailyQuestEraser.schedule(0, 5, aster, aster, aster)
    cron ! WeeklyQuestEraser.schedule(0, 5, aster, aster, DateTimeConstants.MONDAY)
    cron ! MonthlyQuestEraser.schedule(0, 5, 1, aster, aster)
    cron ! MaterialRecordCutter.schedule(17, 3, aster, aster, aster)
    cron ! BasicRecordCutter.schedule(23, 3, aster, aster, aster)
    cron ! CalcScoreInserter.schedule(0, 2, aster, aster, aster)
    cron ! CalcScoreInserter.schedule(0, 14, aster, aster, aster)
    cron ! RankingInserter.schedule(0, aster, aster, aster, aster)
    // 月末にだけやりたいのでとりあえず起動して内部でチェック
    cron ! MonthlyCalcScoreInserter.schedule(0, 22, aster, aster, aster)
    cron ! ShipHistoryCutter.schedule(0, 6, aster, aster, aster)
  }

  private def beforeStart(): Unit = {
    initRanking()
  }

  private def initRanking(): Unit = {
    val now = Ymdh.now(Tokyo)
    val mr = MyfleetRanking.mr
    if(MyfleetRanking.countBy(sqls.eq(mr.yyyymmddhh, now.toInt)) == 0L) RankingInserter.run(now)
  }
}

class MFGCronModule extends AbstractModule {
  override def configure(): Unit = bind(classOf[MFGCron]).asEagerSingleton()
}
