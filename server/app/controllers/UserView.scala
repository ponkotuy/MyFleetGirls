package controllers

import com.github.nscala_time.time.Imports._
import honor.Honors
import models.db.{CalcScore, AGOProgress}
import models.req.{ScoreDays, MaterialDays}
import org.json4s.native.Serialization.write
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import scalikejdbc._
import models.db
import tool.{BattleScore, BestShipExp, HistgramShipLv, STypeExp}
import util.Ymdh

import scala.concurrent.Future

/**
 *
 * @author ponkotuy
 * Date: 14/04/01.
 */
object UserView extends Controller {
  import controllers.Common._
  import DateTime.now

  def name(user: String) = Action.async {
    Future {
      db.Admiral.findByName(user) match {
        case Some(auth) => Redirect(routes.UserView.user(auth.id))
        case _ => NotFound("ユーザが見つかりませんでした")
      }
    }
  }

  def user(memberId: Long) = Action.async {
    Future { Redirect(routes.UserView.top(memberId)) }
  }

  def user2(memberId: Long) = Action.async {
    Future { Redirect(routes.UserView.top(memberId)) }
  }

  def top(memberId: Long) = userView(memberId) { user =>
    val yomes = db.YomeShip.findAllByWithName(sqls"ys.member_id = ${memberId}")
    val yomeIds = yomes.map(_.id).toSet
    val best = db.Ship.findByUserMaxLvWithName(memberId)
      .filterNot(b => yomeIds.contains(b.id))
    val flagship = db.DeckShip.findFlagshipByUserWishShipName(memberId)
      .filterNot(f => (yomeIds ++ best.map(_.id)).contains(f.id))
    val cs = db.CalcScore.cs
    val scores = db.CalcScore.findAllBy(
      sqls.eq(cs.memberId, memberId)
          .and.gt(cs.yyyymmddhh, Ymdh.monthHead(now()).toInt)
    ).sortBy(_.yyyymmddhh)
    val nowScore = BattleScore.calcFromMemberId(memberId).toCalcScore(memberId, 0, System.currentTimeMillis())
    val scoreDays = (CalcScore.zero :: scores ++ List(nowScore)).reverseIterator.sliding(2).map { case Seq(now, prev) =>
      ScoreDays.fromScores(now, prev)
    }.toList
    Ok(views.html.user.user(user, yomes, best, flagship, scoreDays))
  }

  def favorite(memberId: Long) = userView(memberId) { user =>
    val favs = db.Favorite.findAllBy(sqls"f.member_id = $memberId")
    val faved = db.Favorite.countByURL(sqls"f.first = ${"user"} and f.second = ${memberId}")
    Ok(views.html.user.favorite(user, favs, faved))
  }

  def favFroms(memberId: Long, url: String) = userView(memberId) { user =>
    val fromIds = db.Favorite.findAllByUrl(url).map(_.memberId)
    val froms = db.Admiral.findAllIn(fromIds)
    if(froms.nonEmpty) Ok(views.html.user.modal_fav(user, url, froms))
    else BadRequest(s"Not found favorite: memberId = ${memberId}, url = ${url}")
  }

  def snapshot(memberId: Long) = userView(memberId) { user =>
    val snaps = db.DeckSnapshot.findAllOrderWithShip(memberId)
    Ok(views.html.user.snapshot(user, snaps))
  }

  def registerSnap(memberId: Long, deckId: Int) = userView(memberId) { user =>
    val ships = db.DeckShip.findAllByDeck(memberId, deckId)
    db.DeckPort.find(memberId, deckId) match {
      case Some(deck) =>
        if(user.isMine) Ok(views.html.user.register_snap(user, ships, deck))
        else Redirect(routes.View.login(user.admiral.id.toString, routes.UserView.registerSnap(memberId, deckId).url))
      case None => BadRequest(s"Not found deck. memberId = ${memberId}, deckId = ${deckId}")
    }
  }

  def deleteSnap(snapId: Long) = actionAsync { request =>
    db.DeckSnapshot.findWithShip(snapId) match {
      case Some(snap) =>
        val id = snap.memberId
        getUser(id, uuidCheck(id, request.session.get("key"))) match {
          case Some(user) if user.isMine =>
            Ok(views.html.user.snap_delete_pass(user, snap))
          case Some(_) => Redirect(routes.View.login(id.toString, routes.UserView.deleteSnap(snapId).url))
          case None => BadRequest(s"Not found userId = ${id}")
        }
      case None => BadRequest(s"Not found snapId = ${snapId}")
    }
  }

  def material(memberId: Long) = userView(memberId) { user =>
    val day20ago = DateTime.now - 20.days
    val materials = db.Material.findAllByUser(memberId, from = day20ago.getMillis)
    val days = materials.groupBy(m => (new DateTime(m.created) - 5.hours).toLocalDate)
      .mapValues(_.maxBy(_.created))
      .toSeq.sortBy(_._1).reverse
    val materialDays = days.sliding(2).flatMap {
      case Seq(x, y) =>
        val (day, xMat) = x
        val (_, yMat) = y
        Some(MaterialDays.fromMaterials(day, xMat, yMat))
      case _ =>
        None
    }.toSeq
    Ok(views.html.user.material(user, materialDays))
  }

  def ship(memberId: Long) = userView(memberId) { user =>
    val ships = db.Ship.findAllByUserWithName(memberId)
    val decks = db.DeckShip.findAllByUserWithName(memberId)
    val deckports = db.DeckPort.findAllByUser(memberId)
    Ok(views.html.user.ship(user, ships, decks, deckports))
  }

  val UnprocurableItemId = Set(49)

  def book(memberId: Long) = userView(memberId) { user =>
    val allShips = db.ShipBook.findAllUnique()
    val allItems = db.MasterSlotItem.findAllBy(sqls"msi.id <= 500 and msi.id not in (${UnprocurableItemId})")
      .map { msi => msi.id -> msi.name }
    val sBooks = db.ShipBook.findAllBy(sqls"member_id = ${memberId}").map(it => it.indexNo -> it).toMap
    val iBooks = db.ItemBook.findAllBy(sqls"member_id = ${memberId}").map(it => it.indexNo -> it).toMap
    Ok(views.html.user.book(user, allShips, sBooks, allItems, iBooks))
  }

  def shipImageBook(memberId: Long) = userView(memberId) { user =>
    Ok(views.html.user.ship_image_book(user))
  }

  def dock(memberId: Long) = userView(memberId) { user =>
    Ok(views.html.user.dock(user))
  }

  def create(memberId: Long) = userView(memberId) { user =>
    val cShips = db.CreateShip.findAllByUserWithName(memberId, large = true)
    Ok(views.html.user.create(user, cShips))
  }

  def aship(memberId: Long, shipId: Int) = userView(memberId) { user =>
    db.Ship.findByIDWithName(memberId, shipId) match {
      case Some(ship) => Ok(views.html.user.modal_ship(ship, user, false))
      case _ => NotFound("艦娘が見つかりませんでした")
    }
  }

  def snapAship(memberId: Long, shipId: Int) = userView(memberId) { user =>
    db.DeckShipSnapshot.findWithName(shipId) match {
      case Some(ship) => Ok(views.html.user.modal_ship(ship, user, true))
      case _ => NotFound("艦娘が見つかりませんでした")
    }
  }

  def fleet(memberId: Long, deckId: Int) = userView(memberId) { user =>
    val fleet = db.DeckShip.findAllByDeck(memberId, deckId)
    db.DeckPort.find(memberId, deckId) match {
      case Some(deck) => Ok(views.html.user.modal_fleet(fleet, deck, user))
      case _ => NotFound("艦隊が見つかりませんでした")
    }
  }

  def shipPage(memberId: Long, shipId: Int) = userView(memberId) { user =>
    db.Ship.findByIDWithName(memberId, shipId) match {
      case Some(ship) => Ok(views.html.user.modal_ship(ship, user, false))
      case _ => NotFound("艦娘が見つかりませんでした")
    }
  }

  def slotitem(memberId: Long) = userView(memberId) { user =>
    val si = db.SlotItem.si
    val counts = db.SlotItem.countItemBy(sqls.eq(si.memberId, memberId))
    val leveled = db.SlotItem.findAllWithArmedShipBy(sqls.eq(si.memberId, memberId).and.gt(si.level, 0))
    Ok(views.html.user.slotitem(user, counts, leveled))
  }

  def shipslotitem(memberId: Long, itemId: Int) = Action.async {
    Future {
      db.MasterSlotItem.find(itemId) match {
        case Some(item) =>
          val slotItemIds = db.SlotItem.findAllBy(sqls"member_id = ${memberId} and slotitem_id = ${itemId}").map(_.id)
          val ships = db.SlotItem.findAllArmedShipBy(sqls"si.member_id = ${memberId} and si.slotitem_id = ${itemId}")
          Ok(views.html.user.shipslotitem(item, ships, slotItemIds.size))
        case _ => NotFound("Itemが見つかりませんでした")
      }
    }
  }

  def navalBattle(memberId: Long) = userView(memberId) { user =>
    val stages = db.MapRoute.findStageUnique()
    Ok(views.html.user.naval_battle(user, stages))
  }

  def routeLog(memberId: Long) = userView(memberId) { user =>
    Redirect(routes.UserView.navalBattle(memberId))
  }

  def mission(memberId: Long) = userView(memberId) { user =>
    val stages = db.MasterMission.findAll()
    Ok(views.html.user.mission(user, stages))
  }

  def quest(memberId: Long) = userView(memberId) { user =>
    val ago = AGOProgress.find(memberId)
    Ok(views.html.user.quest(user, ago))
  }

  def statistics(memberId: Long) = userView(memberId) { user =>
    val ships = db.Ship.findAllByUserWithName(memberId)
    val stypeExps = STypeExp.fromShips(ships)
    val stypeExpJson = stypeExps.map(_.toJson(memberId))
    val histgramJson = HistgramShipLv.fromShips(ships).map(_.toJsonElem)
    val bestShipExpJson = BestShipExp.fromShips(ships).map(_.toJsonElem)
    Ok(views.html.user.statistics(user, stypeExps, write(stypeExpJson), write(histgramJson), write(bestShipExpJson)))
  }

  def honor(memberId: Long) = userView(memberId) { user =>
    Honors.create(memberId)
    val honors = Honors.fromUser(memberId, false)
    Ok(views.html.user.honor(user, honors))
  }
}
