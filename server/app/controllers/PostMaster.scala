package controllers

import play.api.mvc._
import Common._
import com.ponkotuy.data.master.{MasterMission, MasterShip}

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
object PostMaster extends Controller {
  def ship = checkPonkotuAndParse[List[MasterShip]] { ships =>
    if(ships.size.toLong != models.MasterShip.count()) {
      models.MasterShip.deleteAll()
      ships.foreach { s => models.MasterShip.create(s) }
      Ok("Master Ship All Replaced")
    } else {
      Ok("No Change")
    }
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
}
