package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 15/02/04.
 */
case class RemodelWithName(
    remodel: Remodel,
    before: MasterSlotItem,
    after: MasterSlotItem,
    remodelAfter: Option[RemodelAfterSlot])
