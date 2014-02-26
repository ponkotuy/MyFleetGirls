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
      models.Auth.findByName(user) match {
        case Some(auth) => Redirect(routes.View.index(auth.id))
        case _ => NotFound("ユーザが見つかりませんでした")
      }
    }
  }

  def index(userId: Long) = Action.async {
    Future {
      val user = for {
        auth <- models.Auth.find(userId)
        basic <- models.Basic.findByUser(userId)
      } yield (auth, basic)
      val ships = models.Ship.findAllByUser(userId)
      user match {
        case Some((auth, basic)) => Ok(views.html.index(auth, basic, ships))
        case _ => NotFound("ユーザが見つかりませんでした")
      }
    }
  }
}
