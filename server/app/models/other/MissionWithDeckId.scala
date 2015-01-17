package models.other

import com.ponkotuy.data
import models.db.{Ship, MissionHistoryShip}

/**
 * Created by yosuke on 15/01/15.
 */
case class MissionWithDeckId(deckId: Int, page: Int, number: Int, completeTime: Long, ships: List[Int]) {
  def misshonShips(memberId: Long, missionId: Long, created: Long): List[MissionHistoryShip] = {
    for {
      (sid, i) <- ships.zipWithIndex
      s <- Ship.find(memberId, sid)
    } yield {
      MissionHistoryShip(i + 1, s.shipId, memberId, missionId, s.lv, s.exp, s.nowhp, s.slot.mkString(","), s.fuel, s.bull, created)
    }
  }
}

object MissionWithDeckId {
  def apply(m: data.Mission, deckId: Int, ships: List[Int]): MissionWithDeckId =
    MissionWithDeckId(deckId, m.page, m.number, m.completeTime, ships)
}
