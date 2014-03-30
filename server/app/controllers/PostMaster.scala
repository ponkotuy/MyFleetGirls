package controllers

import play.api.mvc._
import Common._
import com.ponkotuy.data.master.{MasterMission, MasterShip, MasterSlotItem}

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
object PostMaster extends Controller {
  def ship = checkPonkotuAndParse[List[MasterShip]] { ships =>
    models.MasterShip.deleteAll()
    ships.foreach { s => models.MasterShip.create(s) }
    Ok("Master Ship All Replaced")
  }

  def mission = checkPonkotuAndParse[List[MasterMission]] { missions =>
    if(missions.size.toLong != models.MasterMission.count()) {
      models.MasterMission.deleteByMapArea(missions.head.mapArea)
      models.MasterMission.bulkInsert(missions)
      Ok("Master Mission All Replaced")
    } else {
      Ok("No Change")
    }
  }

  def slotitem = checkPonkotuAndParse[List[MasterSlotItem]] { items =>
    models.MasterSlotItem.deleteAll()
    models.MasterSlotItem.bulkInsert(items)
    Ok("Master SlotItem All Replaced")
  }
}
