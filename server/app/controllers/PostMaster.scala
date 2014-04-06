package controllers

import play.api.mvc._
import Common._
import com.ponkotuy.data.master.{MasterSType, MasterMission, MasterShip, MasterSlotItem}

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
object PostMaster extends Controller {
  def ship = checkPonkotuAndParse[List[MasterShip]] { case ships =>
    models.MasterShipBase.deleteAll()
    ships.map(_.base).foreach { b => models.MasterShipBase.create(b) }
    models.MasterShipSpecs.deleteAll()
    models.MasterShipSpecs.bulkInsert(ships.map(_.specs))
    models.MasterShipAfter.deleteAll()
    models.MasterShipAfter.bulkInsert(ships.map(_.after))
    models.MasterShipOther.deleteAll()
    models.MasterShipOther.bulkInsert(ships.map(_.other))
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

  def stype = checkPonkotuAndParse[List[MasterSType]] { stype =>
    models.MasterStype.deleteAll()
    models.MasterStype.bulkInsert(stype)
    Ok("Master SType All Replaced")
  }
}
