package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 14/10/28.
 */
case class RemodelSlotlist(second: Option[Int], list: List[RemodelSlot])

object RemodelSlotlist {
  def fromJson(obj: JValue, second: Option[Int]): RemodelSlotlist = {
    val JArray(xs) = obj
    RemodelSlotlist(second, xs.flatMap(RemodelSlot.fromJson))
  }
}

case class RemodelSlot(
    id: Int,
    slotId: Int,
    fuel: Int,
    ammo: Int,
    steel: Int,
    bauxite: Int,
    develop: Int,
    revamping: Int,
    reqSlotId: Int,
    slotNum: Int
)

object RemodelSlot {
  implicit val format = DefaultFormats

  def fromJson(obj: JValue): Option[RemodelSlot] = {
    obj.transformField {
      case ("api_id", x) => ("id", x)
      case ("api_slot_id", x) => ("slotId", x)
      case ("api_req_fuel", x) => ("fuel", x)
      case ("api_req_bull", x) => ("ammo", x)
      case ("api_req_steel", x) => ("steel", x)
      case ("api_req_bauxite", x) => ("bauxite", x)
      case ("api_req_buildkit", x) => ("develop", x)
      case ("api_req_remodelkit", x) => ("revamping", x)
      case ("api_req_slot_id", x) => ("reqSlotId", x)
      case ("api_req_slot_num", x) => ("slotNum", x)
    }.extractOpt[RemodelSlot]
  }
}
