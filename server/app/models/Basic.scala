package models

import scala.concurrent.Future
import scalikejdbc.async._
import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.WrappedResultSet

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
    created: Long) extends ShortenedNames {
  def save()(implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Basic] =
    Basic.save(this)
}

object Basic extends SQLSyntaxSupport[Basic] with ShortenedNames {
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

  def save(b: Basic)(
    implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Basic] = withSQL {
    update(Basic).set(
      column.userId -> b.userId,
      column.lv -> b.lv, column.experience -> b.experience, column.rank -> b.rank,
      column.maxChara -> b.maxChara, column.fCoin -> b.fCoin,
      column.stWin -> b.stWin, column.stLose -> b.stLose,
      column.msCount -> b.msCount, column.msSuccess -> b.msSuccess,
      column.ptWin -> b.ptWin, column.ptLose -> b.ptLose
    )
  }.update().future.map(_ => b)

  def create(b: data.Basic, userId: Long)(implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Basic] = {
    val created = System.currentTimeMillis()
    val futureId = withSQL {
      insert.into(Basic).namedValues(
        column.userId -> userId,
        column.lv -> b.lv, column.experience -> b.experience, column.rank -> b.rank,
        column.maxChara -> b.maxChara, column.fCoin -> b.fCoin,
        column.stWin -> b.stWin, column.stLose -> b.stLose,
        column.msCount -> b.msCount, column.msSuccess -> b.msSuccess,
        column.ptWin -> b.ptWin, column.ptLose -> b.ptLose,
        column.created -> created
      )
    }.updateAndReturnGeneratedKey().future()
    futureId.map { id =>
      Basic(id, userId,
        b.lv, b.experience, b.rank, b.maxChara, b.fCoin, b.stWin, b.stLose, b.msCount, b.msSuccess, b.ptWin, b.ptLose,
        created)
    }
  }

  def findAllByUser(userId: Long)(
      implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[List[Basic]] = withSQL {
    select.from(Basic as b)
//      .where.eq(b.userId, userId)
      .orderBy(b.created).desc
  }.map(Basic(b)).list().future
}
