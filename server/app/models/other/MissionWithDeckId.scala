package models.other

import com.ponkotuy.data

/**
 * Created by yosuke on 15/01/15.
 */
case class MissionWithDeckId(deckId: Int, page: Int, number: Int, completeTime: Long)

object MissionWithDeckId {
  def apply(m: data.Mission, deckId: Int): MissionWithDeckId = MissionWithDeckId(deckId, m.page, m.number, m.completeTime)
}
