package controllers

import java.util.UUID

import com.ponkotuy.data._
import com.ponkotuy.value.KCServer
import controllers.Common._
import models.req._
import models.db
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
    val isChange = !db.Basic.findByUser(auth.id).exists(_.diff(basic) < 0.01)
    if(isChange) {
      db.Basic.create(basic, auth.id)
      Ok("Success")
    } else {
      Ok("No Change")
    }
  }

  def admiralSettings = authAndParse[KCServer] { case (auth, server) =>
    db.UserSettings.setBase(auth.id, server.number)
    Ok("Success")
  }

  def material = authAndParse[Material] { case (auth, material) =>
    val isChange = !db.Material.findByUser(auth.id).exists(_.diff(material) < 0.03)
    if(isChange) {
      db.Material.create(material, auth.id)
      Ok("Success")
    } else {
      Ok("No Change")
    }
  }

  def ship = Action.async {
    Future { Gone("Obsolete this version API. Your client need updated.") }
  }

  def ship2 = authAndParse[List[Ship]] { case (auth, ships) =>
    db.Ship.deleteAllByUser(auth.id)
    db.Ship.bulkInsert(ships, auth.id)
    Ok("Success")
  }

  def updateShip() = authAndParse[List[Ship]] { case (auth, ships) =>
    db.Ship.bulkUpsert(ships, auth.id)
    Ok("Success")
  }

  def ndock = authAndParse[List[NDock]] { case (auth, docks) =>
    db.NDock.deleteAllByUser(auth.id)
    docks.foreach(dock => db.NDock.create(dock, auth.id))
    Ok("Success")
  }

  def createShip = authAndParse[CreateShipAndDock] { case (auth, CreateShipAndDock(ship, dock)) =>
    try {
      db.CreateShip.createFromKDock(ship, dock, auth.id)
    } catch {
      case e: Throwable =>
        Ok("Duplicate Entry")
    }
    Ok("Success")
  }

  def createShip2 = authAndParse[CreateShipWithId] { case (auth, CreateShipWithId(ship, id)) =>
    db.CreateShip.create(ship, auth.id, id)
    Ok("Success")
  }

  def createItem = authAndParse[CreateItem] { (auth, item) =>
    db.CreateItem.create(item, auth.id)
    for {
      id <- item.id
      slotitemId <- item.slotitemId
      master <- db.MasterSlotItem.find(slotitemId)
    } {
      db.SlotItem.create(auth.id, id, slotitemId, master.name)
    }
    Ok("Success")
  }

  def kdock = authAndParse[List[KDock]] { case (auth, docks) =>
    db.KDock.deleteByUser(auth.id)
    db.KDock.bulkInsert(docks.filterNot(_.completeTime == 0), auth.id)
    Ok("Success")
  }

  def deleteKDock() = authAndParse[DeleteKDock] { case (auth, kdock) =>
    db.KDock.destroy(auth.id, kdock.kDockId)
    Ok("Success")
  }

  def deckPort = authAndParse[List[DeckPort]] { case (auth, decks) =>
    try {
      db.DeckPort.deleteByUser(auth.id)
      db.DeckPort.bulkInsertEntire(decks, auth.id)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    Ok("Success")
  }

  def shipBook = authAndParse[List[ShipBook]] { case (auth, ships) =>
    db.ShipBook.bulkUpsert(ships, auth.id)
    Ok("Success")
  }

  def itemBook = authAndParse[List[ItemBook]] { case (auth, items) =>
    db.ItemBook.bulkUpsert(items, auth.id)
    Ok("Success")
  }

  def mapInfo = authAndParse[List[MapInfo]] { case (auth, maps) =>
    db.MapInfo.deleteAllByUser(auth.id)
    db.MapInfo.bulkInsert(maps, auth.id)
    Ok("Success")
  }

  def slotItem = authAndParse[List[SlotItem]] { case (auth, items) =>
    db.SlotItem.deleteAllByUser(auth.id)
    db.SlotItem.bulkInsert(items, auth.id)
    Ok("Success")
  }

  def battleResult = authAndParse[(BattleResult, MapStart)] { case (auth, (result, map)) =>
    db.BattleResult.create(result, map, auth.id)
    Ok("Success")
  }

  def mapRoute = authAndParse[MapRoute] { case (auth, mapRoute) =>
    db.MapRoute.create(mapRoute, auth.id)
    Ok("Success")
  }

  def questlist = authAndParse[List[Quest]] { case (auth, quests) =>
    db.Quest.bulkUpsert(quests, auth.id)
    Ok("Success")
  }

  def remodelSlot() = authAndParse[RemodelSlotlist] { case (auth, request) =>
    db.RemodelSlot.bulkInsert(request, auth.id)
    Ok("Success")
  }

  def registerSnap() = formAsync { request =>
    RegisterSnapshot.fromReq(request.body) match {
      case Some(snap) =>
        if(uuidCheck(snap.userId, request.session.get("key"))) {
          db.DeckPort.find(snap.userId, snap.deckport) match {
            case Some(deck) =>
              val current = System.currentTimeMillis()
              val deckSnap = db.DeckSnapshot.create(snap.userId, deck.name, snap.title, snap.comment, current)
              val ships = db.DeckShip.findAllByDeck(snap.userId, snap.deckport)
              db.DeckShipSnapshot.bulkInsert(ships.map(_.ship), deckSnap.id)
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
          db.DeckSnapshot.find(snap.snapId) match {
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

  def updateSnap() = formAsync { request =>
    UpdateSnapshot.fromReq(request.body).map { update =>
      if(uuidCheck(update.userId, request.session.get("key"))) {
        db.DeckSnapshot.find(update.snapId).map { snap =>
          db.DeckSnapshot(snap.id, snap.memberId, snap.name, update.title, update.comment, snap.created).save()
          Ok("Success")
        }.getOrElse(BadRequest("Invalid snapId"))
      } else { Unauthorized("Authentication failure") }
    }.getOrElse(BadRequest("Invalid data"))
  }

  def settings = formAsync { request =>
    Settings.fromReq(request.body) match {
      case Some(set: Settings) =>
        if(uuidCheck(set.userId, request.session.get("key"))) {
          db.UserSettings.setYome(set.userId, set.shipId)
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
        val uuid = db.Session.findByUser(auth.userId)
          .map(_.uuid)
          .getOrElse {
            val uuid = UUID.randomUUID()
            db.Session.createByUUID(uuid, auth.userId)
            uuid
          }
        Ok("Success").withSession("key" -> uuid.toString, "memberId" -> auth.userId.toString)
      } else {
        Unauthorized("Authentication failure")
      }
    }.getOrElse { BadRequest("Invalid data") }
  }
}
