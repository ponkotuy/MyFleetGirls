package com.ponkotuy.data

import org.json4s._

/** 図鑑データ
  *
  * 図鑑APIそのものはステータス、Info等、沢山情報が取れるが、要らないのでかなり削っている
  * Date: 14/03/17.
  */
abstract class Book {
  def id: Int
  def indexNo: Int
  def name: String
}

object Book {
  implicit val formats = DefaultFormats
  def fromJson(json: JValue): List[Book] = {
    val JArray(xs) = json \ "api_list"
    checkType(xs.head.asInstanceOf[JObject]) match {
      case BookType.Ship =>
        xs.flatMap(parseShipBook)
      case BookType.Item =>
        xs.map(parseItemBook)
    }
  }

  def parseShipBook(json: JValue): Seq[ShipBook] = {
    val id = parseId(json)
    val JString(name) = json \ "api_name"
    val JArray(statess) = json \ "api_state"
    val indexNo = (json \ "api_index_no").extract[Int]
    (0 until statess.size).flatMap { i =>
      val states = statess(i)
      val JArray(st) = states
      val exist = st(0).extract[Int] != 0
      val isDameged = st(1).extract[Int] != 0
      val isMarried = st(2).extract[Int] != 0
      if(exist) i match {
        case 0 => Some(ShipBook(id, indexNo, isDameged, name, Some(isMarried)))
        case 1 => Some(ShipBook(id, indexNo + 100000, isDameged, name + "改", Some(isMarried)))
        case 2 => Some(ShipBook(id, indexNo + 200000, isDameged, name + "改二", Some(isMarried)))
        case _ => None
      } else None
    }
  }

  def parseItemBook(json: JValue): ItemBook = {
    val id = parseId(json)
    val indexNo = (json \ "api_index_no").extract[Int]
    val JString(name) = json \ "api_name"
    ItemBook(id, indexNo, name)
  }

  private def parseId(json: JValue): Int = {
    val JArray(table_id) = json \ "api_table_id"
    table_id.head.extract[Int]
  }

  private def checkType(json: JObject): BookType = {
    if(json.values.keySet.exists(_ == "api_sinfo")) BookType.Ship
    else BookType.Item
  }
}

case class ShipBook(id: Int, indexNo: Int, isDamaged: Boolean, name: String, isMarried: Option[Boolean]) extends Book

case class ItemBook(id: Int, indexNo: Int, name: String) extends Book

sealed abstract class BookType
object BookType {
  case object Ship extends BookType
  case object Item extends BookType
}
