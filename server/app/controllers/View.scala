package controllers

import build.BuildInfo
import play.api.mvc._
import models.db

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
      val newest = db.Admiral.findNewest(limit = 20)
      val lvTops = db.Admiral.findAllLvTop(limit = 20)
      val source = Source.fromFile("server/public/message")
      val message = try { source.getLines().toList } finally { source.close() }
      val baseCounts = db.UserSettings.countAllByBase()
      Ok(views.html.index(BuildInfo.version, newest, lvTops, message, baseCounts))
    }
  }

  def login(init: String, back: String) = actionAsync { request =>
    request.session.get("key").map { key =>
      Redirect(back).withNewSession
    }.getOrElse {
      val baseCounts = db.UserSettings.countAllByBase()
      Ok(views.html.login(init, back, baseCounts))
    }
  }
}
