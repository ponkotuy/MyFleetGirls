package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/02/25.
 */
case class Ship(
    id: Int, shipId: Int,
    lv: Int, exp: Int, nowhp: Int, slot: List[Int], fuel: Int, bull: Int, dockTime: Long, cond: Int,
    karyoku: Int, raisou: Int, taiku: Int, soukou: Int, kaihi: Int, taisen: Int, sakuteki: Int, lucky: Int, locked: Boolean)

object Ship {
  def fromJson(json: JValue): List[Ship] = {
    val JArray(xs) = json
    implicit def bigint2int(bigint: BigInt): Int = bigint.toInt
    xs.map { x =>
      val JInt(id) = x \ "api_id"
      val JInt(shipId) = x \ "api_ship_id"
      val JInt(lv) = x \ "api_lv"
      val JArray(JInt(exp) :: _) = x \ "api_exp" // [TotalEXP, NextExp, Percentage]
      val JInt(nowhp) = x \ "api_nowhp"
      val JArray(slot) = x \ "api_slot"
      val slotList = slot.map { x => val JInt(s) = x; s.toInt }
      val JInt(fuel) = x \ "api_fuel"
      val JInt(bull) = x \ "api_bull"
      val JInt(ndockTime) = x \ "api_ndock_time"
      val JInt(cond) = x \ "api_cond"
      val JArray(JInt(karyoku) :: _) = x \ "api_karyoku"
      val JArray(JInt(raisou) :: _) = x \ "api_raisou"
      val JArray(JInt(taiku) :: _) = x \ "api_taiku"
      val JArray(JInt(soukou) :: _) = x \ "api_soukou"
      val JArray(JInt(kaihi) :: _) = x \ "api_kaihi"
      val JArray(JInt(taisen) :: _) = x \ "api_taisen"
      val JArray(JInt(sakuteki) :: _) = x \ "api_sakuteki"
      val JArray(JInt(lucky) :: _) = x \ "api_lucky"
      val JInt(locked) = x \ "api_locked"
      Ship(id, shipId,
        lv, exp, nowhp, slotList, fuel, bull, ndockTime.toLong, cond,
        karyoku, raisou, taiku, soukou, kaihi, taisen, sakuteki, lucky,
        locked != BigInt(0))
    }
  }
}
