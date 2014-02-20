package com.ponkotuy.data

import org.json4s.{JValue, DefaultFormats}
import scala.concurrent.Future
import scalikejdbc.async._
import scalikejdbc.SQLInterpolation._


/**
 *
 * @param lv 艦隊司令部Lv
 * @param experience 艦隊司令部経験値
 * @param rank 階級
 * @param maxChara 艦娘保有上限
 * @param fCoin 家具コイン
 * @param stWin 出撃勝敗
 * @param msWin 遠征の成功失敗
 * @param ptWin 演習勝敗
 * @author ponkotuy
 * Date: 14/02/20
 */
case class Basic(
    lv: Int, experience: Int, rank: Int,
    maxChara: Int, fCoin: Int,
    stWin: WinLose, msWin: WinLose, ptWin: WinLose) extends ShortenedNames {
  def save()(implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Basic] =
    Basic.save(this)
}
object Basic extends SQLSyntaxSupport[Basic] with ShortenedNames {
  implicit val formats = DefaultFormats

  def fromJSON(json: JValue): Basic = {
    implicit def toInt(json: JValue) = json.extract[Int]
    val lv = json \ "api_level"
    val experience = json \ "api_experience"
    val rank = json \ "api_rank"
    val maxChara = json \ "api_max_chara"
    val fCoin = json \ "api_fcoin"
    val stWin = WinLose(json \ "api_st_win", json \ "api_st_lose")
    val msCount = json \ "api_ms_count"
    val msSuccess = json \ "api_ms_success"
    val msWin = WinLose(msSuccess, msCount - msSuccess)
    val ptWin = WinLose(json \ "api_pt_win", json \ "api_pt_lose")
    Basic(lv, experience, rank, maxChara, fCoin, stWin, msWin, ptWin)
  }

  def save(b: Basic)(
      implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Basic] = withSQL {
    update(Basic).set(
      column.lv -> b.lv, column.experience -> b.experience, column.rank -> b.rank,
      column.maxChara -> b.maxChara, column.fCoin -> b.fCoin,
      column.stWin -> b.stWin.win, column.stLose -> b.stWin.lose,
      column.msWin -> b.msWin.win, column.msLose -> b.msWin.lose,
      column.ptWin -> b.ptWin.win, column.ptLose -> b.ptWin.lose
    )
  }.update().future.map(_ => b)
}

case class WinLose(win: Int, lose: Int)



