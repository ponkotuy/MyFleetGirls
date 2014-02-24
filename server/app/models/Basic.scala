package models

import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.{DBSession, WrappedResultSet}

/**
 *
 * @param lv 艦隊司令部Lv
 * @param experience 艦隊司令部経験値
 * @param rank 階級
 * @param maxChara 艦娘保有上限
 * @param fCoin 家具コイン
 * @param stWin stLose 出撃勝敗
 * @param msCount msSuccess 遠征回数/勝数
 * @param ptWin ptLose 演習勝敗
 * @author ponkotuy
 * Date: 14/02/20
 */
case class Basic(
    id: Long, userId: Long,
    lv: Int, experience: Int, rank: Int,
    maxChara: Int, fCoin: Int,
    stWin: Int, stLose: Int, msCount: Int, msSuccess: Int, ptWin: Int, ptLose: Int,
    created: Long) {
  def save()(implicit session: DBSession = Basic.autoSession): Basic = Basic.save(this)
}

object Basic extends SQLSyntaxSupport[Basic] {
  lazy val b = Basic.syntax("b")

  def apply(b: SyntaxProvider[Basic])(rs: WrappedResultSet): Basic = apply(b.resultName)(rs)
  def apply(b: ResultName[Basic])(rs: WrappedResultSet): Basic = new Basic(
    id = rs.long(b.id), userId = rs.long(b.userId),
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
        column.userId -> b.userId,
        column.lv -> b.lv, column.experience -> b.experience, column.rank -> b.rank,
        column.maxChara -> b.maxChara, column.fCoin -> b.fCoin,
        column.stWin -> b.stWin, column.stLose -> b.stLose,
        column.msCount -> b.msCount, column.msSuccess -> b.msSuccess,
        column.ptWin -> b.ptWin, column.ptLose -> b.ptLose
      )
    }.update()
    b
  }

  def create(b: data.Basic, userId: Long)(implicit session: DBSession = Basic.autoSession): Basic = {
    val created = System.currentTimeMillis()
    val id = withSQL {
      insert.into(Basic).namedValues(
        column.userId -> userId,
        column.lv -> b.lv, column.experience -> b.experience, column.rank -> b.rank,
        column.maxChara -> b.maxChara, column.fCoin -> b.fCoin,
        column.stWin -> b.stWin, column.stLose -> b.stLose,
        column.msCount -> b.msCount, column.msSuccess -> b.msSuccess,
        column.ptWin -> b.ptWin, column.ptLose -> b.ptLose,
        column.created -> created
      )
    }.updateAndReturnGeneratedKey().apply()
    Basic(id, userId,
      b.lv, b.experience, b.rank, b.maxChara, b.fCoin, b.stWin, b.stLose, b.msCount, b.msSuccess, b.ptWin, b.ptLose,
      created)
  }

  /** 特定ユーザの最新1件を取得
   */
  def findByUser(userId: Long)(implicit session: DBSession = Basic.autoSession): Option[Basic] = withSQL {
    select.from(Basic as b)
      .where.eq(b.userId, userId)
      .orderBy(b.created).desc
      .limit(1)
  }.map(Basic(b)).headOption().apply()

  def findAllByUser(userId: Long)(implicit session: DBSession = Basic.autoSession): List[Basic] = withSQL {
    select.from(Basic as b)
      .where.eq(b.userId, userId)
      .orderBy(b.created).desc
  }.map(Basic(b)).list().apply()
}
