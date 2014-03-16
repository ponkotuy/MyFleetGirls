package controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import build.BuildInfo

/**
 *
 * @author ponkotuy
 * Date 14/02/24
 */
object View extends Controller {
  def name(user: String) = Action.async {
    Future {
      models.Admiral.findByName(user) match {
        case Some(auth) => Redirect(routes.View.user(auth.id))
        case _ => NotFound("ユーザが見つかりませんでした")
      }
    }
  }

  def user(memberId: Long) = Action.async {
    Future {
      val user = for {
        auth <- models.Admiral.find(memberId)
        basic <- models.Basic.findByUser(memberId)
      } yield (auth, basic)
      val ships = models.Ship.findAllByUserWithMaster(memberId)
      val ndocks = models.NDock.fineAllByUserWithName(memberId)
      val kdocks = models.KDock.findAllByUserWithName(memberId)
      val cShips = models.CreateShip.findAllByUserWithName(memberId)
      val cItems = models.CreateItem.findAllByUserWithName(memberId)
      val missions = models.Mission.findByUserWithName(memberId)
      user match {
        case Some((auth, basic)) => Ok(views.html.user(auth, basic, ships, ndocks, kdocks, cShips, cItems, missions))
        case _ => NotFound("ユーザが見つかりませんでした")
      }
    }
  }

  def index = Action.async {
    Future {
      val newest = models.Admiral.findAll().sortBy(_.created).reverse
      val lvTops = models.Admiral.findAllLvTop()
      Ok(views.html.index(BuildInfo.version, newest, lvTops))
    }
  }

  def about = Action { Ok(views.html.about()) }
}
