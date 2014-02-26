package controllers

import play.api.mvc._
import Common._
import com.ponkotuy.data.MasterShip

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
object PostMaster extends Controller {
  def ship = Action.async { request =>
    checkPonkotu(request) {
      withData[List[MasterShip]](request) { ships =>
        if(ships.size.toLong != models.MasterShip.count()) {
          models.MasterShip.deleteAll()
          ships.foreach { s => models.MasterShip.create(s) }
          println("Master Ship All Cleared")
        }
      }
    }
  }
}
