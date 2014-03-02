package controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
 *
 * @author ponkotuy
 * Date 14/02/24
 */
object View extends Controller {
  def name(user: String) = Action.async {
    Future {
      models.Admiral.findByName(user) match {
        case Some(auth) => Redirect(routes.View.index(auth.id))
        case _ => NotFound("ユーザが見つかりませんでした")
      }
    }
  }

  def index(memberId: Long) = Action.async {
    Future {
      val user = for {
        auth <- models.Admiral.find(memberId)
        basic <- models.Basic.findByUser(memberId)
      } yield (auth, basic)
      val ships = models.Ship.findAllByUserWithMaster(memberId)
      val docks = models.NDock.fineAllByUserWithName(memberId)
      println(docks)
      user match {
        case Some((auth, basic)) => Ok(views.html.index(auth, basic, ships, docks))
        case _ => NotFound("ユーザが見つかりませんでした")
      }
    }
  }
}
