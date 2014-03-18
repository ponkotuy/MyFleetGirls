package com.ponkotuy.data

import org.json4s._

/** 図鑑データ
  *
  * 図鑑APIそのものはステータス、Info等、沢山情報が取れるが、要らないのでかなり削っている
  * Date: 14/03/17.
  */
abstract class Book {
  def memberId: Long
  def id: Int
  def indexNo: Int
  def name: String
}

object Book {
  implicit val formats = DefaultFormats
  def fromJson(json: JValue): List[Book] = {
    val memberId = (json \ "api_member_id").extract[Long]
    val dispType = (json \ "api_disp_type").extract[Int]
    val JArray(xs) = json \ "api_list"
    dispType match {
      case 1 =>
        xs.flatMap(parseShipBook(memberId))
      case 2 =>
        xs.flatMap(parseItemBook(memberId))
      case _ => Nil
    }
  }

  def parseShipBook(memberId: Long)(json: JValue): Seq[ShipBook] = {
    val id = (json \ "api_id").extractOpt[Int].getOrElse(-1)
    if(id != -1) {
      val JString(name) = json \ "api_name"
      val JArray(statess) = json \ "api_state"
      val indexNo = (json \ "api_index_no").extract[Int]
      (0 until statess.size).flatMap { i =>
        val states = statess(i)
        val JArray(st) = states
        val exist = st(0).extract[Int] != 0
        val isDameged = st(1).extract[Int] != 0
        if(exist) i match {
          case 0 => Some(ShipBook(memberId, id, indexNo, isDameged, name))
          case 1 => Some(ShipBook(memberId, id, indexNo + 100000, isDameged, name + "改"))
          case 2 => Some(ShipBook(memberId, id, indexNo + 200000, isDameged, name + "改二"))
          case _ => None
        } else None
      }
    } else Nil
  }

  def parseItemBook(memberId: Long)(json: JValue): Option[ItemBook] = {
    val id = (json \ "api_id").extractOpt[Int].getOrElse(-1)
    if(id != -1) {
      val indexNo = (json \ "api_index_no").extract[Int]
      val JString(name) = json \ "api_name"
      Some(ItemBook(memberId, id, indexNo, name))
    } else None
  }
}

case class ShipBook(memberId: Long, id: Int, indexNo: Int, isDamaged: Boolean, name: String) extends Book

case class ItemBook(memberId: Long, id: Int, indexNo: Int, name: String) extends Book
