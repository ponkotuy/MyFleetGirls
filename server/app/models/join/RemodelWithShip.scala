package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 14/10/29.
 */
case class RemodelWithShip(remodel: RemodelSlot, ship: Option[MasterShipBase])
