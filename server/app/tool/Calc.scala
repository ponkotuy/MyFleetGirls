package tool

import models.join.ShipParameter

/**
 *
 * @author ponkotuy
 * Date: 14/10/14.
 */
object Calc {
  import EquipType._

  val OtherAircraft = Set(Aircraft: _*) -- Set(Scouts: _*)

  def spotter(ships: Iterable[ShipParameter]): Int = {
    val spots = ships.flatMap { ship =>
      ship.slotMaster.flatMap { slot => slot.category.map { cat => cat -> slot.search}}
    }
    val scots = spots.filter { case (cat, _) => Scouts.contains(cat)}.map(_._2).sum
    val radars = spots.filter { case (cat, _) => Radars.contains(cat)}.map(_._2).sum
    val aircrafts = spots.filter { case (cat, _) => OtherAircraft.contains(cat) }.map(_._2).sum
    val sum = ships.map(_.sakuteki).sum
    scots*2 + radars + aircrafts*2 + math.sqrt(sum - radars - scots).toInt
  }
}
