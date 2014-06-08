package com.ponkotuy.data

import com.ponkotuy.tool.Pretty

/**
 *
 * @author ponkotuy
 * Date: 14/03/05.
 */
case class CreateShipAndDock(createShip: CreateShip, kDock: KDock)

case class CreateShipWithId(createShip: CreateShip, resultShip: Int) {
  def summary: String = Pretty(Map(("資材", createShip.materialSummary), ("艦娘ID", resultShip)))
}
