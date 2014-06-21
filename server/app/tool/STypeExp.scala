package tool

import dat.ShipWithName
import models.MasterStype

/**
 * Date: 14/06/21.
 */
case class STypeExp(stype: MasterStype, exp: Long) {
  def name = stype.name
  def toJsonElem = Map[String, Any]("label" -> name, "data" -> exp)
}

object STypeExp {
  val stype = Map(6 -> 5, 9 -> 8, 10 -> 8, 14 -> 13, 16 -> 7, 18 -> 11, 20 -> 7).withDefault(identity)

  def fromShips(ships: Seq[ShipWithName]): Seq[STypeExp] = {
    val stypes = models.MasterStype.findAll().map(st => st.id -> st).toMap
    ships.groupBy(s => stype(s.stype.id))
      .mapValues(_.map(_.exp.toLong).sum)
      .toSeq.sortBy(_._2).reverse
      .map { case (stId, exp) => STypeExp(stypes(stId), exp)}
  }
}

case class HistgramShipLv(lv: Int, count: Int) {
  def toJsonElem = Array(lv, count)
}

object HistgramShipLv {
  def fromShips(ships: Seq[ShipWithName]): Seq[HistgramShipLv] = {
    ships.groupBy(_.lv/10)
      .map { case (lv10, xs) => HistgramShipLv(lv10 * 10, xs.size) }
      .toSeq.sortBy(_.lv)
  }
}

case class BestShipExp(best: Int, rate: Double) {
  def toJsonElem = Array(best, rate)
}

object BestShipExp {
  def fromShips(ships: Seq[ShipWithName]): Seq[BestShipExp] = {
    val sumExp = ships.map(_.exp).sum.toDouble
    val sorted = ships.sortBy(_.exp).reverse
    val sums = sorted.map(_.exp).foldLeft(List(0)) { (xs, exp) =>
      xs.head + exp :: xs
    }
    sums.reverse.tail.zipWithIndex.map { case (sum, i) =>
      BestShipExp(i, sum / sumExp * 100)
    }.take(100)
  }
}
