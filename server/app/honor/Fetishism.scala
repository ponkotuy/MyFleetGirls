package honor

import ranking.common.EvolutionBase

import scala.collection.breakOut

object Fetishism extends HonorCategory {
  import com.ponkotuy.value.ShipIds._

  override def category: Int = 18

  override def comment: String = "対象艦娘の合計Lvを一定以上"

  val groups = Vector(
    FetiGroup("メガネフェチ", Set(Mochizuki, Chokai, Kirishima, I8, Makigumo, Musashi, Katori, Ooyodo, Roma), 450)
  )

  override def approved(memberId: Long, db: HonorCache): List[String] = {
    val ships = db.shipWithName
    val lvs = ships.map { ship =>
      EvolutionBase(ship.shipId) -> ship.lv
    }
    groups.filter { gr =>
      val userSumLv = lvs.filter { case (sid, _) => gr.ships.contains(sid) }
        .map { case (_, lv) => lv.toInt }.sum
      userSumLv >= gr.sumLv
    }.map(_.name)(breakOut)
  }
}

case class FetiGroup(name: String, ships: Set[Int], sumLv: Int)
