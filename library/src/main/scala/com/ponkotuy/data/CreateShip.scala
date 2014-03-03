package com.ponkotuy.data

/**
 *
 * @author ponkotuy
 * Date: 14/03/03.
 */
case class CreateShip(
    fuel: Int, ammo: Int, steel: Int, bauxite: Int, develop: Int,
    kDock: Int, highspeed: Boolean, largeFlag: Boolean)

object CreateShip {
  def fromMap(map: Map[String, String]): CreateShip = {
    CreateShip(
      fuel = map("api_item1").toInt,
      ammo = map("api_item2").toInt,
      steel = map("api_item3").toInt,
      bauxite = map("api_item4").toInt,
      develop = map("api_item5").toInt,
      kDock = map("api_kdock_id").toInt,
      highspeed = map("api_highspeed").toInt != 0,
      largeFlag = map("api_large_flag").toInt != 0
    )
  }
}
