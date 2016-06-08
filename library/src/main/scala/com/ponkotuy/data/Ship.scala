package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/02/25.
 */
case class Ship(
    id: Int,
    shipId: Int,
    lv: Int,
    exp: Int,
    nowhp: Int,
    maxhp: Int,
    slot: List[Int],
    kyouka: List[Int],
    back: Int,
    fuel: Int,
    bull: Int,
    dockTime: Long,
    cond: Int,
    karyoku: Int,
    raisou: Int,
    taiku: Int,
    soukou: Int,
    kaihi: Int,
    taisen: Int,
    sakuteki: Int,
    lucky: Int,
    locked: Boolean)

object Ship {
  implicit val formats = DefaultFormats
  def fromJson(json: JValue): List[Ship] = {
    json.extractOrElse[List[RawShip]](Nil).flatMap(_.build)
  }

  private case class RawShip(
      api_id: Int,
      api_ship_id: Int,
      api_lv: Int,
      api_exp: List[Int],
      api_nowhp: Int,
      api_maxhp: Int,
      api_slot: List[Int],
      api_kyouka: List[Int],
      api_backs: Int,
      api_fuel: Int,
      api_bull: Int,
      api_ndock_time: Long,
      api_cond: Int,
      api_karyoku: List[Int],
      api_raisou: List[Int],
      api_taiku: List[Int],
      api_soukou: List[Int],
      api_kaihi: List[Int],
      api_taisen: List[Int],
      api_sakuteki: List[Int],
      api_lucky: List[Int],
      api_locked: Int
  ) {
    def build: Option[Ship] = {
      for {
        exp <- api_exp.headOption
        karyoku <- api_karyoku.headOption
        raisou <- api_raisou.headOption
        taiku <- api_taiku.headOption
        soukou <- api_soukou.headOption
        kaihi <- api_kaihi.headOption
        taisen <- api_taisen.headOption
        sakuteki <- api_sakuteki.headOption
        lucky <- api_lucky.headOption
      } yield {
        Ship(
          id = api_id,
          shipId = api_ship_id,
          lv = api_lv,
          exp = exp,
          nowhp = api_nowhp,
          maxhp = api_maxhp,
          slot = api_slot,
          kyouka = api_kyouka,
          back = api_backs,
          fuel = api_fuel,
          bull = api_bull,
          dockTime = api_ndock_time,
          cond = api_cond,
          karyoku = karyoku,
          raisou = raisou,
          taiku = taiku,
          soukou = soukou,
          kaihi = kaihi,
          taisen = taisen,
          sakuteki = sakuteki,
          lucky = lucky,
          locked = api_locked != 0
        )
      }
    }
  }
}
