package controllers

import com.github.nscala_time.time.Imports._
import org.json4s.native.Serialization.write
import play.api.mvc._
import scalikejdbc._
import tool.{BestShipExp, HistgramShipLv, MaterialDays, STypeExp}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
 *
 * @author ponkotuy
 * Date: 14/04/01.
 */
object UserView extends Controller {
  import controllers.Common._

  def name(user: String) = Action.async {
    Future {
      models.Admiral.findByName(user) match {
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
    val yome = models.UserSettings.findYome(memberId)
    val best = models.Ship.findByUserMaxLvWithName(memberId)
      .filterNot(b => b.id == yome.fold(-1)(_.id))
    val flagship = models.DeckShip.findFlagshipByUserWishShipName(memberId)
      .filterNot(f => Set(yome, best).flatten.map(_.id).contains(f.id))
    Ok(views.html.user.user(user, yome, best, flagship))
  }

  def favorite(memberId: Long) = userView(memberId) { user =>
    val favs = models.Favorite.findAllBy(sqls"f.member_id = $memberId")
    val faved = models.Favorite.countByURL(sqls"f.first = ${"user"} and f.second = ${memberId}")
    Ok(views.html.user.favorite(user, favs, faved))
  }

  def snapshot(memberId: Long) = userView(memberId) { user =>
    val snaps = models.DeckSnapshot.findAllByWithShip(sqls"member_id = ${memberId}")
    Ok(views.html.user.snapshot(user, snaps))
  }

  def registerSnap(memberId: Long, deckId: Int) = userView(memberId) { user =>
    val ships = models.DeckShip.findAllByDeck(memberId, deckId)
    models.DeckPort.find(memberId, deckId) match {
      case Some(deck) =>
        if(user.isMine) Ok(views.html.user.register_snap(user, ships, deck))
        else Redirect(routes.View.login(user.admiral.id.toString, routes.UserView.registerSnap(memberId, deckId).url))
      case None => BadRequest(s"Not found deck. memberId = ${memberId}, deckId = ${deckId}")
    }
  }

  def deleteSnap(snapId: Long) = actionAsync { request =>
    models.DeckSnapshot.findWithShip(snapId) match {
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
    val materials = models.Material.findAllByUser(memberId, from = day20ago.getMillis)
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
    val ships = models.Ship.findAllByUserWithName(memberId)
    val decks = models.DeckShip.findAllByUserWithName(memberId)
    val deckports = models.DeckPort.findAllByUser(memberId)
    Ok(views.html.user.ship(user, ships, decks, deckports))
  }

  def settings(memberId: Long) = userView(memberId) { user =>
    if(user.isMine) Ok(views.html.user.settings(user))
    else Redirect(routes.View.login(user.admiral.id.toString, routes.UserView.settings(memberId).url))
  }

  def book(memberId: Long) = userView(memberId) { user =>
    val sBooks = models.ShipBook.findAllBy(sqls"member_id = ${memberId}").sortBy(_.indexNo)
    val iBooks = models.ItemBook.findAllBy(sqls"member_id = ${memberId}").sortBy(_.indexNo)
    Ok(views.html.user.book(user, sBooks, iBooks))
  }

  def dock(memberId: Long) = userView(memberId) { user =>
    Ok(views.html.user.dock(user))
  }

  def create(memberId: Long) = userView(memberId) { user =>
    val cShips = models.CreateShip.findAllByUserWithName(memberId, large = true)
    Ok(views.html.user.create(user, cShips))
  }

  def aship(memberId: Long, shipId: Int) = userView(memberId) { user =>
    models.Ship.findByIDWithName(memberId, shipId) match {
      case Some(ship) => Ok(views.html.user.modal_ship(ship, user))
      case _ => NotFound("艦娘が見つかりませんでした")
    }
  }

  def snapAship(memberId: Long, shipId: Int) = userView(memberId) { user =>
    models.DeckShipSnapshot.findWithName(shipId) match {
      case Some(ship) => Ok(views.html.user.modal_ship(ship, user))
      case _ => NotFound("艦娘が見つかりませんでした")
    }
  }

  def fleet(memberId: Long, deckId: Int) = userView(memberId) { user =>
    val fleet = models.DeckShip.findAllByDeck(memberId, deckId)
    models.DeckPort.find(memberId, deckId) match {
      case Some(deck) => Ok(views.html.user.modal_fleet(fleet, deck, user))
      case _ => NotFound("艦隊が見つかりませんでした")
    }
  }

  def shipPage(memberId: Long, shipId: Int) = userView(memberId) { user =>
    models.Ship.findByIDWithName(memberId, shipId) match {
      case Some(ship) => Ok(views.html.user.modal_ship(ship, user))
      case _ => NotFound("艦娘が見つかりませんでした")
    }
  }

  def slotitem(memberId: Long) = userView(memberId) { user =>
    val counts = models.SlotItem.countItemBy(sqls"member_id = ${memberId}")
    val leveled = models.SlotItem.findAllWithArmedShipBy(sqls"si.member_id = ${memberId} and level > 0")
    Ok(views.html.user.slotitem(user, counts, leveled))
  }

  def shipslotitem(memberId: Long, itemId: Int) = Action.async {
    Future {
      models.MasterSlotItem.find(itemId) match {
        case Some(item) =>
          val slotItemIds = models.SlotItem.findAllBy(sqls"member_id = ${memberId} and slotitem_id = ${itemId}").map(_.id)
          val ships = models.SlotItem.findAllArmedShipBy(sqls"si.member_id = ${memberId} and si.slotitem_id = ${itemId}")
          Ok(views.html.user.shipslotitem(item, ships, slotItemIds.size))
        case _ => NotFound("Itemが見つかりませんでした")
      }
    }
  }

  def navalBattle(memberId: Long) = userView(memberId) { user =>
    Ok(views.html.user.naval_battle(user))
  }

  def routeLog(memberId: Long) = userView(memberId) { user =>
    val stages = models.MapRoute.findStageUnique()
    Ok(views.html.user.route_log(user, stages))
  }

  def quest(memberId: Long) = userView(memberId) { user =>
    Ok(views.html.user.quest(user))
  }

  def statistics(memberId: Long) = userView(memberId) { user =>
    val ships = models.Ship.findAllByUserWithName(memberId)
    val stypeExps = STypeExp.fromShips(ships)
    val stypeExpJson = stypeExps.map(_.toJson(memberId))
    val histgramJson = HistgramShipLv.fromShips(ships).map(_.toJsonElem)
    val bestShipExpJson = BestShipExp.fromShips(ships).map(_.toJsonElem)
    Ok(views.html.user.statistics(user, stypeExps, write(stypeExpJson), write(histgramJson), write(bestShipExpJson)))
  }
}
