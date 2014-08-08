package controllers

import java.util.UUID

import com.ponkotuy.data._
import com.ponkotuy.value.KCServer
import controllers.Common._
import dat.{AuthDataImpl, DeleteSnapshot, RegisterSnapshot, Settings}
import play.api.mvc._
import tool.Authentication

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
 *
 * @author ponkotuy
 * Date: 14/02/21.
 */
object Post extends Controller {
  def basic = authAndParse[Basic] { case (auth, basic) =>
    val isChange = !models.Basic.findByUser(auth.id).exists(_.diff(basic) < 0.01)
    if(isChange) {
      models.Basic.create(basic, auth.id)
      Ok("Success")
    } else {
      Ok("No Change")
    }
  }

  def admiralSettings = authAndParse[KCServer] { case (auth, server) =>
    models.UserSettings.setBase(auth.id, server.number)
    Ok("Success")
  }

  def material = authAndParse[Material] { case (auth, material) =>
    val isChange = !models.Material.findByUser(auth.id).exists(_.diff(material) < 0.03)
    if(isChange) {
      models.Material.create(material, auth.id)
      Ok("Success")
    } else {
      Ok("No Change")
    }
  }

  def ship = Action.async {
    Future { Gone("Obsolete this version API. Your client need updated.") }
  }

  def ship2 = authAndParse[List[Ship]] { case (auth, ships) =>
    models.Ship.deleteAllByUser(auth.id)
    models.Ship.bulkInsert(ships, auth.id)
    Ok("Success")
  }

  def updateShip() = authAndParse[List[Ship]] { case (auth, ships) =>
    models.Ship.bulkUpsert(ships, auth.id)
    Ok("Success")
  }

  def ndock = authAndParse[List[NDock]] { case (auth, docks) =>
    models.NDock.deleteAllByUser(auth.id)
    docks.foreach(dock => models.NDock.create(dock, auth.id))
    Ok("Success")
  }

  def createShip = authAndParse[CreateShipAndDock] { case (auth, CreateShipAndDock(ship, dock)) =>
    try {
      models.CreateShip.createFromKDock(ship, dock, auth.id)
    } catch {
      case e: Throwable =>
        Ok("Duplicate Entry")
    }
    Ok("Success")
  }

  def createShip2 = authAndParse[CreateShipWithId] { case (auth, CreateShipWithId(ship, id)) =>
    models.CreateShip.create(ship, auth.id, id)
    Ok("Success")
  }

  def createItem = authAndParse[CreateItem] { (auth, item) =>
    models.CreateItem.create(item, auth.id)
    for {
      id <- item.id
      slotitemId <- item.slotitemId
      master <- models.MasterSlotItem.find(slotitemId)
    } {
      models.SlotItem.create(auth.id, id, slotitemId, master.name)
    }
    Ok("Success")
  }

  def kdock = authAndParse[List[KDock]] { case (auth, docks) =>
    models.KDock.deleteByUser(auth.id)
    models.KDock.bulkInsert(docks.filterNot(_.completeTime == 0), auth.id)
    Ok("Success")
  }

  def deleteKDock() = authAndParse[DeleteKDock] { case (auth, kdock) =>
    models.KDock.destroy(auth.id, kdock.kDockId)
    Ok("Success")
  }

  def deckPort = authAndParse[List[DeckPort]] { case (auth, decks) =>
    try {
      models.DeckPort.deleteByUser(auth.id)
      models.DeckPort.bulkInsert(decks, auth.id)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    Ok("Success")
  }

  def shipBook = authAndParse[List[ShipBook]] { case (auth, ships) =>
    models.ShipBook.bulkUpsert(ships, auth.id)
    Ok("Success")
  }

  def itemBook = authAndParse[List[ItemBook]] { case (auth, items) =>
    models.ItemBook.bulkUpsert(items, auth.id)
    Ok("Success")
  }

  def mapInfo = authAndParse[List[MapInfo]] { case (auth, maps) =>
    models.MapInfo.deleteAllByUser(auth.id)
    models.MapInfo.bulkInsert(maps, auth.id)
    Ok("Success")
  }

  def slotItem = authAndParse[List[SlotItem]] { case (auth, items) =>
    models.SlotItem.deleteAllByUser(auth.id)
    models.SlotItem.bulkInsert(items, auth.id)
    Ok("Success")
  }

  def battleResult = authAndParse[(BattleResult, MapStart)] { case (auth, (result, map)) =>
    models.BattleResult.create(result, map, auth.id)
    Ok("Success")
  }

  def mapRoute = authAndParse[MapRoute] { case (auth, mapRoute) =>
    models.MapRoute.create(mapRoute, auth.id)
    Ok("Success")
  }

  def questlist = authAndParse[List[Quest]] { case (auth, quests) =>
    models.Quest.bulkUpsert(quests, auth.id)
    Ok("Success")
  }

  def registerSnap() = formAsync { request =>
    RegisterSnapshot.fromReq(request.body) match {
      case Some(snap) =>
        if(uuidCheck(snap.userId, request.session.get("key"))) {
          models.DeckPort.find(snap.userId, snap.deckport) match {
            case Some(deck) =>
              val current = System.currentTimeMillis()
              val deckSnap = models.DeckSnapshot.create(snap.userId, deck.name, snap.title, snap.comment, current)
              val ships = models.DeckShip.findAllByDeck(snap.userId, snap.deckport)
              models.DeckShipSnapshot.bulkInsert(ships.map(_.ship), deckSnap.id)
              Ok("Success")
            case None => BadRequest("Invalid deckport")
          }
        } else {
          Unauthorized("Authentication failure")
        }
      case None => BadRequest("Invalid data")
    }
  }

  def deleteSnap() = formAsync { request =>
    DeleteSnapshot.fromReq(request.body) match {
      case Some(snap) =>
        if(uuidCheck(snap.userId, request.session.get("key"))) {
          models.DeckSnapshot.find(snap.snapId) match {
            case Some(deck) =>
              deck.destroy()
              Ok("Success")
            case None => BadRequest("Invalid snapId")
          }
        } else {
          Unauthorized("Authorication failure")
        }
      case None => BadRequest("Invalid data")
    }
  }

  def settings = formAsync { request =>
    Settings.fromReq(request.body) match {
      case Some(set: Settings) =>
        if(uuidCheck(set.userId, request.session.get("key"))) {
          models.UserSettings.setYome(set.userId, set.shipId)
          Ok("Success")
        } else {
          Unauthorized("Authentication failure")
        }
      case None =>
        BadRequest("Invalid data")
    }
  }

  def setSession() = formAsync { request =>
    AuthDataImpl.fromReq(request.body).map { auth =>
      if(Authentication.myfleetAuth(auth)) {
        val uuid = models.Session.findByUser(auth.userId)
          .map(_.uuid)
          .getOrElse {
            val uuid = UUID.randomUUID()
            models.Session.createByUUID(uuid, auth.userId)
            uuid
          }
        Ok("Success").withSession("key" -> uuid.toString)
      } else {
        Unauthorized("Authentication failure")
      }
    }.getOrElse { BadRequest("Invalid data") }
  }
}
