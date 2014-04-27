package com.ponkotuy.data

/**
 *
 * @param shipIdx : 0-indexed
 * @author ponkotuy
 * Date: 14/04/26.
 */
case class HenseiChange(shipIdx: Int, verno: Int, shipId: Int, id: Int)

object HenseiChange {
  def fromMap(obj: Map[String, String]): HenseiChange = {
    HenseiChange(
      shipIdx = obj("api_ship_idx").toInt,
      verno = obj("api_verno").toInt,
      shipId = obj("api_ship_id").toInt,
      id = obj("api_id").toInt
    )
  }
}
