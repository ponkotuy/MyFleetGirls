package com.ponkotuy.data

import org.json4s.JsonAST.JValue
import org.json4s.DefaultFormats

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
case class Basic(lv: Int, experience: Int, rank: Int, maxChara: Int, fCoin: Int, stWin: WinLose, msWin: WinLose, ptWin: WinLose)
object Basic {
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
}

case class WinLose(win: Int, lose: Int)

/** このツール内でログイン代わりに使うパラメータ
  *
  * @param id nick name id
  * @param startTime ゲーム開始時間っぽいけど暗号代わりに
 */
case class Auth(id: Long, nickname: String, startTime: Long)
object Auth {
  implicit val formats = DefaultFormats

  def fromJSON(json: JValue): Auth = {
    val id = (json \ "api_nickname_id").extract[String]
    val nickname = (json \ "api_nickname").extract[String]
    val startTime = (json \ "api_starttime").extract[Long]
    Auth(id.toLong, nickname, startTime)
  }
}
