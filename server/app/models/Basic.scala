package models

import scalikejdbc._
import com.ponkotuy.data
import scalikejdbc.{DBSession, WrappedResultSet}
import tool.DiffCalc

/**
 *
 * @param lv 艦隊司令部Lv
 * @param experience 艦隊司令部経験値
 * @param rank 階級
 * @param maxChara 艦娘保有上限
 * @param fCoin 家具コイン
 * @param stWin stLose 出撃勝敗
 * @param msCount msSuccess 遠征回数/成功数
 * @param ptWin ptLose 演習勝敗
 * @author ponkotuy
 * Date: 14/02/20
 */
case class Basic(
    id: Long, memberId: Long,
    lv: Int, experience: Int, rank: Int,
    maxChara: Int, fCoin: Int,
    stWin: Int, stLose: Int, msCount: Int, msSuccess: Int, ptWin: Int, ptLose: Int,
    created: Long) {
  def save()(implicit session: DBSession = Basic.autoSession): Basic = Basic.save(this)

  /**
   * 新規挿入の判断等に使う差分情報
   */
  def diff(x: data.Basic): Double = {
    import DiffCalc._
    Iterator(
      neq(lv, x.lv),
      ratio(experience, x.experience),
      neq(rank, x.rank),
      ratio(fCoin, x.fCoin),
      ratio(stWin, x.stWin),
      ratio(stLose, x.stLose),
      ratio(msCount, x.msCount),
      ratio(ptWin, x.ptWin),
      ratio(ptLose, x.ptLose)
    ).max
  }
}

object Basic extends SQLSyntaxSupport[Basic] {
  lazy val b = Basic.syntax("b")

  def apply(b: SyntaxProvider[Basic])(rs: WrappedResultSet): Basic = apply(b.resultName)(rs)
  def apply(b: ResultName[Basic])(rs: WrappedResultSet): Basic = new Basic(
    id = rs.long(b.id), memberId = rs.long(b.memberId),
    lv = rs.int(b.lv), experience = rs.int(b.experience), rank = rs.int(b.rank),
    maxChara = rs.int(b.maxChara), fCoin = rs.int(b.fCoin),
    stWin = rs.int(b.stWin), stLose = rs.int(b.stLose),
    msCount = rs.int(b.msCount), msSuccess = rs.int(b.msSuccess),
    ptWin = rs.int(b.ptWin), ptLose = rs.int(b.ptLose),
    created = rs.long(b.created)
  )

  def save(b: Basic)(implicit session: DBSession = Basic.autoSession): Basic = {
    withSQL {
      update(Basic).set(
        column.memberId -> b.memberId,
        column.lv -> b.lv, column.experience -> b.experience, column.rank -> b.rank,
        column.maxChara -> b.maxChara, column.fCoin -> b.fCoin,
        column.stWin -> b.stWin, column.stLose -> b.stLose,
        column.msCount -> b.msCount, column.msSuccess -> b.msSuccess,
        column.ptWin -> b.ptWin, column.ptLose -> b.ptLose
      )
    }.update()
    b
  }

  def create(b: data.Basic, memberId: Long)(implicit session: DBSession = Basic.autoSession): Long = {
    val created = System.currentTimeMillis()
    withSQL {
      insert.into(Basic).namedValues(
        column.memberId -> memberId,
        column.lv -> b.lv, column.experience -> b.experience, column.rank -> b.rank,
        column.maxChara -> b.maxChara, column.fCoin -> b.fCoin,
        column.stWin -> b.stWin, column.stLose -> b.stLose,
        column.msCount -> b.msCount, column.msSuccess -> b.msSuccess,
        column.ptWin -> b.ptWin, column.ptLose -> b.ptLose,
        column.created -> created
      )
    }.updateAndReturnGeneratedKey().apply()
  }

  /** 特定ユーザの最新1件を取得
   */
  def findByUser(memberId: Long)(implicit session: DBSession = Basic.autoSession): Option[Basic] = withSQL {
    select.from(Basic as b)
      .where.eq(b.memberId, memberId)
      .orderBy(b.created).desc
      .limit(1)
  }.map(Basic(b)).headOption().apply()

  def findAllByUser(memberId: Long)(implicit session: DBSession = Basic.autoSession): List[Basic] = withSQL {
    select.from(Basic as b)
      .where.eq(b.memberId, memberId)
      .orderBy(b.created).desc
  }.map(Basic(b)).list().apply()
}
