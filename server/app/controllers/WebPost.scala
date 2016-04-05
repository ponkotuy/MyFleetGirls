package controllers

import java.util.UUID
import javax.inject.Inject

import controllers.form.SetSnapshotOrder
import models.db
import models.join.ShipWithName
import models.req._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import scalikejdbc._
import tool.Authentication

import scala.concurrent.ExecutionContext

/**
 *
 * @author ponkotuy
 * Date: 15/02/04.
 */
class WebPost @Inject()(val messagesApi: MessagesApi, implicit val ec: ExecutionContext) extends Controller with I18nSupport {
  import controllers.Common._

  def registerSnap() = formAsync { request =>
    RegisterSnapshot.fromReq(request.body) match {
      case Some(snap) =>
        if(uuidCheck(snap.userId, request.session.get("key"))) {
          if(snap.deckport < 10) registerDefaultSnap(snap) else registerCombinedSnap(snap)
        } else Res.authFail
      case None => BadRequest("Invalid data")
    }
  }

  private def registerDefaultSnap(snap: RegisterSnapshot) = {
    db.DeckPort.find(snap.userId, snap.deckport) match {
      case Some(deck) =>
        val current = System.currentTimeMillis()
        val deckSnap = db.DeckSnapshot.create(snap.userId, deck.name, snap.title, snap.comment, current)
        val ships = db.DeckShip.findAllByDeck(snap.userId, snap.deckport)
        registerSnapCommon(deckSnap, ships)
      case None => BadRequest("Invalid deckport")
    }
  }

  private def registerCombinedSnap(snap: RegisterSnapshot) = {
    val deckIds = Seq(snap.deckport / 10, snap.deckport % 10)
    val dp = db.DeckPort.dp
    val ports = db.DeckPort.findAllBy(sqls.eq(dp.memberId, snap.userId).and.in(dp.id, deckIds))
    val deckSnap = db.DeckSnapshot.create(snap.userId, ports.head.name, snap.title, snap.comment, System.currentTimeMillis())
    val ships = deckIds.flatMap { deckId => db.DeckShip.findAllByDeck(snap.userId, deckId) }
    registerSnapCommon(deckSnap, ships)
  }

  private def registerSnapCommon(deck: db.DeckSnapshot, ships: Seq[ShipWithName]) = {
    ships.zipWithIndex.map { case (ship, idx) =>
      val shipSnap = db.DeckShipSnapshot.createFromShip(ship.ship, deck.id, (idx + 1).toShort)
      val now = System.currentTimeMillis()
      val items = ship.slot.zipWithIndex.map { case (item, i) =>
        item.itemSnapshot(shipSnap.id, i + 1, now)
      }
      db.ItemSnapshot.batchInsert(items)
    }
    db.SnapshotText.create(deck)
    Res.success
  }


  def deleteSnap() = formAsync { request =>
    DeleteSnapshot.fromReq(request.body) match {
      case Some(snap) =>
        if(uuidCheck(snap.userId, request.session.get("key"))) {
          db.DeckSnapshot.find(snap.snapId) match {
            case Some(deck) =>
              db.SnapshotText.find(deck.id).foreach(_.destroy())
              val dss = db.DeckShipSnapshot.column
              val ships = db.DeckShipSnapshot.findAllBy(sqls.eq(dss.deckId, deck.id))
              val is = db.ItemSnapshot.column
              ships.foreach { ship =>
                db.ItemSnapshot.destroyBy(sqls.eq(is.shipSnapshotId, ship.id))
              }
              db.DeckShipSnapshot.destroyBy(sqls.eq(dss.deckId, deck.id))
              deck.destroy()
              Res.success
            case None => BadRequest("Invalid snapId")
          }
        } else Res.authFail
      case None => BadRequest("Invalid data")
    }
  }

  def updateSnap() = formAsync { request =>
    UpdateSnapshot.fromReq(request.body).map { update =>
      if(uuidCheck(update.userId, request.session.get("key"))) {
        db.DeckSnapshot.find(update.snapId).map { snap =>
          db.DeckSnapshot(snap.id, snap.memberId, snap.name, update.title, update.comment, snap.created, snap.sortOrder).save()
          Ok("Success")
        }.getOrElse(BadRequest("Invalid snapId"))
      } else Res.authFail
    }.getOrElse(BadRequest("Invalid data"))
  }

  def setOrder() = formAsync { implicit req =>
    def f(order: SetSnapshotOrder) = {
      if(!uuidCheck(order.memberId, req.session.get("key"))) Res.authFail
      else {
        val ordered = db.DeckSnapshot.findAllOrder(order.memberId)
        val idx = ordered.indexWhere(_.id == order.snapId)
        if(idx == -1) BadRequest(s"Not found snapId = ${order.snapId}")
        else {
          val snap = ordered(idx)
          if(order.sortOrder > 0) {
            ordered.lift(idx + 1).fold(BadRequest("Not found destination")) { dest =>
              snap.copy(sortOrder = snap.sortOrder + 1).save()
              dest.copy(sortOrder = dest.sortOrder - 1).save()
              Res.success
            }
          } else {
            ordered.lift(idx - 1).fold(BadRequest("Not found destination")) { dest =>
              snap.copy(sortOrder = snap.sortOrder - 1).save()
              dest.copy(sortOrder = dest.sortOrder + 1).save()
              Res.success
            }
          }
        }
      }
    }
    SetSnapshotOrder.form.bindFromRequest().fold(_ => BadRequest("Invalid data"), f)
  }

  def settings = formAsync { request =>
    Settings.fromReq(request.body) match {
      case Some(set: Settings) =>
        if(uuidCheck(set.userId, request.session.get("key"))) {
          val yomes = db.YomeShip.findAllFromMemberId(set.userId)
          if(yomes.exists(_.shipId == set.shipId)) { // 既に嫁がいたら削除
            destroyYome(set.userId, set.shipId)
          } else if(yomes.size < db.YomeShip.MaxYomeCount) {
            db.YomeShip.create(set.userId, (yomes.size + 1).toShort, set.shipId)
          } else {
            db.YomeShip(set.userId, yomes.size.toShort, set.shipId).save()
          }
          Res.success
        } else Res.authFail
      case None =>
        BadRequest("Invalid data")
    }
  }

  def deleteYome() = formAsync { request =>
    Settings.fromReq(request.body).map { case Settings(memberId, shipId) =>
      if(uuidCheck(memberId, request.session.get("key"))) {
        if(destroyYome(memberId, shipId)) Res.success
        else BadRequest("Not found ship")
      } else Res.authFail
    }.getOrElse(BadRequest("Invalid data"))
  }

  private def destroyYome(memberId: Long, shipId: Int): Boolean = {
    val yomes = db.YomeShip.findAllBy(sqls"ys.member_id = ${memberId}")
    val result = yomes.filterNot(_.shipId == shipId).map(_.shipId)
    yomes.foreach(_.destroy())
    result.zipWithIndex.foreach { case (sid, idx) =>
      db.YomeShip.create(memberId, (idx + 1).toShort, sid)
    }
    yomes.size > result.size
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
      } else Res.authFail
    }.getOrElse { BadRequest("Invalid data") }
  }

  def setHonor() = formAsync { implicit req =>
    def set(setHonor: SetHonor) = {
      if(uuidCheck(setHonor.memberId, req.session.get("key"))) {
        execHonor(setHonor.memberId, setHonor.name) { honor =>
          db.Honor.updateUnset(setHonor.memberId)
          honor.copy(setBadge = true).save()
          Res.success
        }
      } else Res.authFail
    }
    SetHonor.form.bindFromRequest().fold(form => BadRequest(form.errorsAsJson), set)
  }

  def honorInvisible() = formAsync { implicit req =>
    def patch(patchInvisible: PatchInvisible) = {
      if(uuidCheck(patchInvisible.memberId, req.session.get("key"))) {
        execHonor(patchInvisible.memberId, patchInvisible.name) { honor =>
          patchInvisible.value.fold(honor.copy(invisible = !honor.invisible)){ v => honor.copy(invisible = v) }.save()
          Res.success
        }
      } else Res.authFail
    }
    PatchInvisible.form.bindFromRequest().fold(form => BadRequest(form.errorsAsJson), patch)
  }

  private def execHonor(memberId: Long, name: String)(f: db.Honor => Result): Result =
    db.Honor.findName(memberId, name).fold(BadRequest(s"Invalid name=${name}"))(f)

  def questManual() = formAsync { implicit req =>
    def patch(m: PatchManual) = {
      if(uuidCheck(m.memberId, req.session.get("key"))) {
        db.Quest.find(m.id, m.memberId).fold(BadRequest(s"Invalid id=${m.id}")) { q =>
          val manual = m.value.getOrElse(!q.manualFlag)
          q.copy(manualFlag = manual).save()
          Res.success
        }
      } else Res.authFail
    }
    PatchManual.form.bindFromRequest().fold(form => BadRequest(form.errorsAsJson), patch)
  }
}
