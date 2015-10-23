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
 * @param medals 甲勲章有無
 * @author ponkotuy, lyrical_logical
 * Date: 14/02/20
 */
case class Basic(
    lv: Int,
    experience: Int,
    rank: Int,
    maxChara: Int,
    fCoin: Int,
    stWin: Int, stLose: Int,
    msCount: Int, msSuccess: Int,
    ptWin: Int, ptLose: Int,
    medals: Int,
    comment: String,
    deckCount: Int,
    kdockCount: Int,
    ndockCount: Int,
    largeDock: Boolean) {
  def summary: String = Pretty(Map(("Lv", lv), ("経験値", experience)))
}

object Basic {
  implicit val formats = DefaultFormats

  def fromJSON(json: JValue): Basic = json.extract[RawBasic].build

  private case class RawBasic(
      api_level: Int,
      api_experience: Int,
      api_rank: Int,
      api_max_chara: Int,
      api_fcoin: Int,
      api_st_win: Int,
      api_st_lose: Int,
      api_ms_count: Int,
      api_ms_success: Int,
      api_pt_win: Int,
      api_pt_lose: Int,
      api_medals: Int,
      api_comment: String,
      api_count_deck: Int,
      api_count_kdock: Int,
      api_count_ndock: Int,
      api_large_dock: Option[Int]) {
    def build: Basic = {
      Basic(
        lv = api_level,
        experience = api_experience,
        rank = api_rank,
        maxChara = api_max_chara,
        fCoin = api_fcoin,
        stWin = api_st_win,
        stLose = api_st_lose,
        msCount = api_ms_count,
        msSuccess = api_ms_success,
        ptWin = api_pt_win,
        ptLose = api_pt_lose,
        medals = api_medals,
        comment = api_comment,
        deckCount = api_count_deck,
        kdockCount = api_count_kdock,
        ndockCount = api_count_ndock,
        largeDock = !api_large_dock.contains(0))
    }
  }
}
