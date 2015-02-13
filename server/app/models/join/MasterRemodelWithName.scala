package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 15/02/14.
 */
case class MasterRemodelWithName(master: MasterRemodel, slotitem: MasterSlotItem, use: Option[MasterSlotItem])
