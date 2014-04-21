package com.ponkotuy.data

import org.json4s._
import com.ponkotuy.tool.Pretty

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
    memberId: Long, lv: Int, experience: Int, rank: Int,
    maxChara: Int, fCoin: Int,
    stWin: Int, stLose: Int, msCount: Int, msSuccess: Int, ptWin: Int, ptLose: Int) {
  def summary: String = Pretty(Map("Lv" -> lv, "経験値" -> experience))
}

object Basic {
  implicit val formats = DefaultFormats

  def fromJSON(json: JValue): Basic = {
    implicit def toInt(json: JValue) = json.extract[Int]
    val JString(memberId) = json \ "api_member_id"
    val lv = json \ "api_level"
    val experience = json \ "api_experience"
    val rank = json \ "api_rank"
    val maxChara = json \ "api_max_chara"
    val fCoin = json \ "api_fcoin"
    val stWin = json \ "api_st_win"
    val stLose = json \ "api_st_lose"
    val msCount = json \ "api_ms_count"
    val msSuccess = json \ "api_ms_success"
    val ptWin = json \ "api_pt_win"
    val ptLose: Int = json \ "api_pt_lose"
    Basic(memberId.toLong,
      lv, experience, rank, maxChara, fCoin, stWin, stLose, msCount, msSuccess, ptWin, ptLose)
  }
}
