package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import play.api.mvc._
import scalikejdbc._
import org.json4s.native.Serialization.write
import com.github.nscala_time.time.Imports._

/**
 *
 * @author ponkotuy
 * Date: 14/04/01.
 */
object UserView {
  import Common._

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

  def snapshot(memberId: Long) = userView(memberId) { user =>
    val snaps = models.DeckSnapshot.findAllByWithShip(sqls"member_id = ${memberId}")
    Ok(views.html.user.snapshot(user, snaps))
  }

  def registerSnap(memberId: Long, deckId: Int) = userView(memberId) { user =>
    val ships = models.DeckShip.findAllByDeck(memberId, deckId)
    models.DeckPort.find(memberId, deckId) match {
      case Some(deck) =>
        Ok(views.html.user.register_snap(user, ships, deck))
    }
  }

  def deleteSnap(snapId: Long) = actionAsync {
    models.DeckSnapshot.findWithShip(snapId) match {
      case Some(snap) =>
        getUser(snap.memberId) match {
          case Some(user) =>
            Ok(views.html.user.snap_delete_pass(user, snap))
          case None => BadRequest(s"Not found userId = ${snap.memberId}")
        }
      case None => BadRequest(s"Not found snapId = ${snapId}")
    }
  }

  def material(memberId: Long) = userView(memberId) { user =>
    val day20ago = DateTime.now - 20.days
    val materials = models.Material.findAllByUser(memberId, from = day20ago.millis)
    val days = materials.groupBy(m => (new DateTime(m.created) - 5.hours).toLocalDate)
      .mapValues(_.maxBy(_.created))
      .toSeq.sortBy(_._1).reverse
    val materialDays = days.sliding(2).map { case Seq(x, y) =>
      val (day, xMat) = x
      val (_, yMat) = y
      MaterialDays.fromMaterials(day, xMat, yMat)
    }.toSeq
    Ok(views.html.user.material(user, materialDays))
  }

  def ship(memberId: Long) = userView(memberId) { user =>
    val ships = models.Ship.findAllByUserWithName(memberId)
    val decks = models.DeckShip.findAllByUserWithName(memberId)
    val deckports = models.DeckPort.findAllByUser(memberId)
    Ok(views.html.user.ship(user, ships, decks, deckports))
  }

  def settings(memberId: Long) = userView(memberId) { user => Ok(views.html.user.settings(user)) }

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
      case Some(ship) => Ok(views.html.user.modal_ship(ship))
      case _ => NotFound("艦娘が見つかりませんでした")
    }
  }

  def snapAship(memberId: Long, shipId: Int) = userView(memberId) { user =>
    models.DeckShipSnapshot.findWithName(shipId) match {
      case Some(ship) => Ok(views.html.user.modal_ship(ship))
      case _ => NotFound("艦娘が見つかりませんでした")
    }
  }

  def fleet(memberId: Long, deckId: Int) = userView(memberId) { user =>
    val fleet = models.DeckShip.findAllByDeck(memberId, deckId)
    models.DeckPort.find(memberId, deckId) match {
      case Some(deck) => Ok(views.html.user.modal_fleet(fleet, deck))
      case _ => NotFound("艦隊が見つかりませんでした")
    }
  }

  def shipPage(memberId: Long, shipId: Int) = userView(memberId) { user =>
    models.Ship.findByIDWithName(memberId, shipId) match {
      case Some(ship) => Ok(views.html.user.modal_ship(ship))
      case _ => NotFound("艦娘が見つかりませんでした")
    }
  }

  def slotitem(memberId: Long) = userView(memberId) { user =>
    val counts = models.SlotItem.countItemBy(sqls"member_id = ${memberId}")
    Ok(views.html.user.slotitem(user, counts))
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

  val stype = Map(6 -> 5, 9 -> 8, 10 -> 8, 14 -> 13, 16 -> 7, 18 -> 11, 20 -> 7).withDefault(identity)
  def statistics(memberId: Long) = userView(memberId) { user =>
    val ships = models.Ship.findAllByUserWithName(memberId)
    val stypeExps = ships.groupBy(s => stype(s.stype.id))
      .mapValues(_.map(_.exp.toLong).sum)
      .toSeq.sortBy(_._2).reverse
    val stypes = models.MasterStype.findAll().map(st => st.id -> st.name).toMap
    val stypeExpJson = stypeExps.map { case (st, exp) =>
      Map("label" -> stypes(st), "data" -> exp)
    }
    Ok(views.html.user.statistics(user, write(stypeExpJson)))
  }
}
