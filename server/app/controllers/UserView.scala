package controllers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import play.api.mvc._
import scalikejdbc.SQLInterpolation._

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
    models.Ship.findByUserMaxLvWithName(memberId) match {
      case Some(best) =>
        models.DeckShip.findFlagshipByUserWishShipName(memberId) match {
          case Some(flagship) => Ok(views.html.user(user, best, flagship))
          case _ => NotFound("旗艦を登録していません")
        }
      case _ => NotFound("艦娘を登録していません")
    }
  }

  def material(memberId: Long) = userView(memberId) { user =>
    Ok(views.html.material(user))
  }

  def ship(memberId: Long) = userView(memberId) { user =>
    val ships = models.Ship.findAllByUserWithName(memberId)
    Ok(views.html.ship(user, ships))
  }

  def book(memberId: Long) = userView(memberId) { user =>
    val sBooks = models.ShipBook.findAllBy(sqls"member_id = ${memberId}").sortBy(_.indexNo)
    val iBooks = models.ItemBook.findAllBy(sqls"member_id = ${memberId}").sortBy(_.indexNo)
    Ok(views.html.book(user, sBooks, iBooks))
  }

  def dock(memberId: Long) = userView(memberId) { user =>
    val ndocks = models.NDock.fineAllByUserWithName(memberId)
    val kdocks = models.KDock.findAllByUserWithName(memberId)
    val missions = models.Mission.findByUserWithName(memberId)
    Ok(views.html.dock(user, ndocks, kdocks, missions))
  }

  def create(memberId: Long) = userView(memberId) { user =>
    val cShips = models.CreateShip.findAllByUserWithName(memberId, large = true)
    Ok(views.html.create(user, cShips))
  }

  def aship(memberId: Long, shipId: Int) = userView(memberId) {
    user =>
      models.Ship.findByIDWithName(memberId, shipId) match {
        case Some(ship) => Ok(views.html.modal_ship(ship))
        case _ => NotFound("艦娘が見つかりませんでした")
      }
  }

  def slotitem(memberId: Long) = userView(memberId) { user =>
    val counts = models.SlotItem.countItemBy(sqls"member_id = ${memberId}")
    Ok(views.html.slotitem(user, counts))
  }
}