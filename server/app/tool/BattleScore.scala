package tool

import models.db.{CellInfo, BattleResult, Basic}
import com.github.nscala_time.time.Imports._
import models.join.Stage
import scalikejdbc._

/**
  * Date:12/23/15.
  * @author ponkotuy
  */
case class BattleScore(monthlyExp: Int, yearlyExp: Int, eo: Int, lastEo: Int) {
  def pp: String = {
    s"${sum}(${toString})"
  }

  def sum: Int = monthlyExp + yearlyExp + eo + lastEo
}

object BattleScore {
  case class FromExp(monthly: Int, yearly: Int)

  object FromExp {
    def empty = FromExp(0, 0)
  }

  def calcFromMemberId(memberId: Long): BattleScore = {
    val exp = fromExp(memberId)
    val now = DateTime.now()
    val eo = calcEo(memberId, new Interval(monthHead(now), now))
    val lastEo = calcEo(memberId, new Interval(monthHead(now - 1.month), monthHead(now))) / 35
    BattleScore(exp.monthly, exp.yearly, eo, lastEo)
  }

  private def fromExp(memberId: Long): FromExp = {
    val b = Basic.b
    val now = DateTime.now()
    Basic.findByUser(memberId).map(_.experience).fold(FromExp.empty) { nowExp =>
      val lastMonthExp =
        Basic.findExpBy(sqls.eq(b.memberId, memberId).and.lt(b.created, monthHead(now).getMillis), b.created.desc).getOrElse(nowExp)
      val lastYearExp =
        Basic.findExpBy(sqls.eq(b.memberId, memberId).and.lt(b.created, yearHead(now).getMillis), b.created.desc).getOrElse(lastMonthExp)
      val monthly = (nowExp - lastMonthExp) * 7 / 10000
      val yearly = (lastMonthExp - lastYearExp) / 50000
      FromExp(monthly, yearly)
    }
  }

  private def calcEo(memberId: Long, interval: Interval): Int = {
    StageInfo.values.map { info =>
      val count = clearCount(memberId, info.stage, interval)
      if (info.clear <= count) info.score else 0
    }.sum
  }

  private def clearCount(memberId: Long, stage: Stage, interval: Interval): Long = {
    val ci = CellInfo.ci
    val cell = CellInfo.findAllBy(sqls.eq(ci.areaId, stage.area).and.eq(ci.infoNo, stage.info))
    val bosses = cell.filter(_.boss).map(_.cell)
    val br = BattleResult.br
    BattleResult.countBy(
      sqls.eq(br.memberId, memberId)
          .and.eq(br.areaId, stage.area)
          .and.eq(br.infoNo, stage.info)
          .and.in(br.cell, bosses)
          .and.gt(br.created, interval.startMillis).and.lt(br.created, interval.endMillis)
    )
  }

  private def monthHead(now: DateTime): DateTime = {
    new LocalDate(now.getYear, now.getMonthOfYear, 1).toDateTime(LocalTime.MIDNIGHT)
  }

  private def yearHead(now: DateTime): DateTime = {
    new LocalDate(now.getYear, 1, 1).toDateTime(LocalTime.MIDNIGHT)
  }
}

/**
  * @param score Clear score
  * @param clear Required clear count
  */
case class StageInfo(stage: Stage, score: Int, clear: Int)

object StageInfo {
  val values = Vector(
    StageInfo(Stage(1, 5), 75, 4),
    StageInfo(Stage(1, 6), 75, 7),
    StageInfo(Stage(2, 5), 100, 4),
    StageInfo(Stage(3, 5), 150, 4),
    StageInfo(Stage(4, 5), 180, 5),
    StageInfo(Stage(5, 5), 200, 5)
  )
}
