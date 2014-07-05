package controllers

import build.BuildInfo
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.io.Source

/**
 *
 * @author ponkotuy
 * Date 14/02/24
 */
object View extends Controller {
  import Common._

  def index = Action.async {
    Future {
      val newest = models.Admiral.findNewest(limit = 20)
      val lvTops = models.Admiral.findAllLvTop(limit = 20)
      val message = Source.fromFile("server/public/message").getLines()
      val baseCounts = models.UserSettings.countAllByBase()
      println(baseCounts)
      Ok(views.html.index(BuildInfo.version, newest, lvTops, message, baseCounts))
    }
  }

  def about = Common.actionAsync { Ok(views.html.about()) }

  def login(init: String, back: String) = actionAsync { request =>
    request.session.get("key").map { key =>
      Redirect(back).withNewSession
    }.getOrElse {
      Ok(views.html.login(init, back))
    }
  }
}
