package dat

import models._

/**
 *
 * @author ponkotuy
 * Date: 14/10/22.
 */
case class SlotItemWithMaster(item: SlotItem, master: MasterSlotItem) {
  def id = master.id
  def name = master.name
  def category = master.category
}
