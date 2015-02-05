package com.ponkotuy.data.master

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 15/02/05.
 */
case class MasterRemodel(
    develop: Int,
    remodel: Int,
    certainDevelop: Int,
    certainRemodel: Int,
    slotitemId: Int,
    slotitemNum: Int,
    changeFlag: Boolean,
    origSlotId: Int)

object MasterRemodel {
  implicit val formats = DefaultFormats

  private case class RawMasterRemodel(
      api_req_buildkit: Int,
      api_req_remodelkit: Int,
      api_certain_buildkit: Int,
      api_certain_remodelkit: Int,
      api_req_slot_id: Int,
      api_req_slot_num: Int,
      api_change_flag: Int) {
    def build(origSlotId: Int): MasterRemodel =
      MasterRemodel(
        api_req_buildkit,
        api_req_remodelkit,
        api_certain_buildkit,
        api_certain_remodelkit,
        api_req_slot_id,
        api_req_slot_num,
        api_change_flag != 0,
        origSlotId)
  }

  def fromJson(obj: JValue, req: Map[String, String]): Option[MasterRemodel] = {
    for {
      raw <- obj.extractOpt[RawMasterRemodel]
      slotId <- req.get("api_slot_id")
    } yield {
      raw.build(slotId.toInt)
    }
  }
}
