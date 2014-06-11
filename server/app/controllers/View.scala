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
  def index = Action.async {
    Future {
      val newest = models.Admiral.findNewest(limit = 20)
      val lvTops = models.Admiral.findAllLvTop(limit = 20)
      val message = Source.fromFile("server/public/message").getLines()
      Ok(views.html.index(BuildInfo.version, newest, lvTops, message))
    }
  }

  def about = Action { Ok(views.html.about()) }
}
