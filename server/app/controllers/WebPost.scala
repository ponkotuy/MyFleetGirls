package controllers

import java.util.UUID

import models.db
import models.req._
import play.api.mvc._
import scalikejdbc._
import tool.Authentication

/**
 *
 * @author ponkotuy
 * Date: 15/02/04.
 */
object WebPost extends Controller {
  import controllers.Common._

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
          val count = db.YomeShip.countBy(sqls"ys.member_id = ${set.userId}")
          if(count < db.YomeShip.MaxYomeCount) {
            db.YomeShip.create(set.userId, (count + 1).toShort, set.shipId)
          } else {
            db.YomeShip(set.userId, count.toShort, set.shipId).save()
          }
          Ok("Success")
        } else {
          Unauthorized("Authentication failure")
        }
      case None =>
        BadRequest("Invalid data")
    }
  }

  def deleteYome() = formAsync { request =>
    Settings.fromReq(request.body).map { case Settings(memberId, shipId) =>
      if(uuidCheck(memberId, request.session.get("key"))) {
        db.YomeShip.findShipId(memberId, shipId) match {
          case Some(yome) =>
            yome.destroy()
            Ok("Success")
          case None => BadRequest("Not found ship")
        }
      } else {
        Unauthorized("Authentication failure")
      }
    }.getOrElse(BadRequest("Invalid data"))
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
