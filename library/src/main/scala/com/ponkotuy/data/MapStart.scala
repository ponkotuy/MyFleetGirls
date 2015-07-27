package com.ponkotuy.data

import org.json4s._
import scala.util.Try
import com.ponkotuy.tool.Pretty

/**
 *
 * @param mapAreaId : Stage 5-3の5
 * @param mapInfoNo : Stage 5-3の3
 * @param next : next CellID？
 * @param bossCellNo : Boss CellID？
 * @param bossComp : ボス倒したflag？
 * @author ponkotuy
 * Date: 14/04/19.
 */
case class MapStart(
    rashinFlag: Boolean,
    rashinId: Int,
    mapAreaId: Int,
    mapInfoNo: Int,
    no: Int,
    eventId: Int,
    next: Int,
    bossCellNo: Int,
    bossComp: Boolean,
    enemyId: Option[Int]) {
  def summary: String = Pretty(
    Map(
      ("Map", s"$mapAreaId-$mapInfoNo-$no"),
      ("Next", next)
    )
  )
}

object MapStart {
  implicit val default = DefaultFormats
  def fromJson(obj: JValue): MapStart = {
    val rashinFlag = (obj \ "api_rashin_flg").extract[Int] != 0
    val rashinId = (obj \ "api_rashin_id").extract[Int]
    val mapAreaId = (obj \ "api_maparea_id").extract[Int]
    val mapInfoNo = (obj \ "api_mapinfo_no").extract[Int]
    val no = (obj \ "api_no").extract[Int]
    val eventId = (obj \ "api_event_id").extract[Int]
    val next = (obj \ "api_next").extract[Int]
    val bossCellNo = (obj \ "api_bosscell_no").extract[Int]
    val bossComp = (obj \ "api_bosscomp").extract[Int] != 0
    val enemyId = Try { (obj \ "api_enemy" \ "api_enemy_id").extract[Int] }.toOption
    MapStart(rashinFlag, rashinId, mapAreaId, mapInfoNo, no, eventId, next, bossCellNo, bossComp, enemyId)
  }
}
