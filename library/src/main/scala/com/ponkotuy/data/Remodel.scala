package com.ponkotuy.data

import org.json4s._

/**
 *
 * @author ponkotuy
 * Date: 15/01/30.
 */
case class Remodel(
    flag: Boolean,
    beforeItemId: Int,
    afterItemId: Int,
    voiceId: Int,
    afterSlot: Option[RemodelAfterSlot],
    useSlotIds: List[Int],
    certain: Boolean,
    slotId: Int)

object Remodel {
  implicit val format = DefaultFormats

  private case class RawRemodel(
      api_remodel_flag: Int,
      api_remodel_id: List[Int],
      api_voice_id: String,
      api_after_slot: Option[RawRemodelAfterSlot],
      api_use_slot_id: List[Int]) {
    def build(certain: Boolean, slotId: Int): Remodel = {
      val List(beforeItem, afterItem) = api_remodel_id
      Remodel(
        api_remodel_flag != 0,
        beforeItem,
        afterItem,
        api_voice_id.toInt,
        api_after_slot.map(_.build),
        api_use_slot_id,
        certain,
        slotId
      )
    }
  }

  private case class RawRemodelAfterSlot(api_id: Int, api_slotitem_id: Int, api_locked: Int, api_level: Int) {
    def build: RemodelAfterSlot = RemodelAfterSlot(api_id, api_slotitem_id, api_locked != 0, api_level)
  }

  def fromJson(obj: JValue, req: Map[String, String]): Option[Remodel] = {
    for {
      raw <- obj.extractOpt[RawRemodel]
      certain <- req.get("api_certain_flag")
      slotId <- req.get("api_slot_id")
    } yield raw.build(certain != "0", slotId.toInt)
  }
}

case class RemodelAfterSlot(id: Int, slotitemId: Int, locked: Boolean, level: Int)
