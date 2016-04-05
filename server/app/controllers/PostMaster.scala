package controllers

import javax.inject.Inject

import com.ponkotuy.data.master._
import controllers.Common._
import models.db
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
class PostMaster @Inject()(implicit val ec: ExecutionContext) extends Controller {
  def ship = checkPonkotuAndParse[List[MasterShip]] { case ships =>
    db.MasterShipBase.deleteAll()
    ships.map(_.base).foreach { b => db.MasterShipBase.create(b) }
    db.MasterShipSpecs.deleteAll()
    db.MasterShipSpecs.bulkInsert(ships.map(_.specs))
    db.MasterShipAfter.deleteAll()
    db.MasterShipAfter.bulkInsert(ships.map(_.after))
    db.MasterShipOther.deleteAll()
    db.MasterShipOther.bulkInsert(ships.map(_.other))
    Ok("Master Ship All Replaced")
  }

  def mission = checkPonkotuAndParse[List[MasterMission]] { missions =>
    db.MasterMission.deleteAll()
    db.MasterMission.bulkInsert(missions)
    Ok("Master Mission All Replaced")
  }

  def slotitem = checkPonkotuAndParse[List[MasterSlotItem]] { items =>
    db.MasterSlotItem.deleteAll()
    db.MasterSlotItem.bulkInsert(items)
    Ok("Master SlotItem All Replaced")
  }

  def stype = checkPonkotuAndParse[List[MasterSType]] { stype =>
    db.MasterStype.deleteAll()
    db.MasterStype.bulkInsert(stype)
    Ok("Master SType All Replaced")
  }
}
