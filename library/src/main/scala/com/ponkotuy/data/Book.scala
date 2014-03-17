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
}

object Book {
  implicit val formats = DefaultFormats
  def fromJson(json: JValue): List[Book] = {
    val memberId = (json \ "api_member_id").extract[Int]
    val dispType = (json \ "api_disp_type").extract[Int]
    val JArray(xs) = json \ "api_list"
    dispType match {
      case 1 =>
        xs.flatMap { x =>
          val id = (x \ "api_id").extractOpt[Int].getOrElse(-1)
          if(id != -1) {
            val indexNo = (x \ "api_index_no").extract[Int]
            val JArray(statess) = x \ "api_state"
            val JArray(states) = statess.head
            val isDameged = states(1).extract[Int] != 0
            Some(ShipBook(memberId, id, indexNo, isDameged))
          } else None
        }
      case 2 =>
        xs.flatMap { x =>
          val id = (x \ "api_id").extractOpt[Int].getOrElse(-1)
          if(id != -1) {
            val indexNo = (x \ "api_index_no").extract[Int]
            Some(ItemBook(memberId, id, indexNo))
          } else None
        }
      case _ => Nil
    }
  }
}

case class ShipBook(memberId: Long, id: Int, indexNo: Int, isDamaged: Boolean) extends Book

case class ItemBook(memberId: Long, id: Int, indexNo: Int) extends Book
