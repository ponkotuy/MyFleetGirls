package controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import org.json4s._
import org.json4s.native.Serialization.write
import build.BuildInfo
import models.Mat

/**
 *
 * @author ponkotuy
 * Date 14/02/24
 */
object View extends Controller {
  implicit val formats = DefaultFormats
  def index = Action.async {
    Future {
      val newest = models.Admiral.findAll(limit = 20).sortBy(_.created).reverse
      val lvTops = models.Admiral.findAllLvTop(limit = 20)
      Ok(views.html.index(BuildInfo.version, newest, lvTops))
    }
  }

  def about = Action { Ok(views.html.about()) }

  def statistics = Action.async {
    Future {
      val sCounts = models.CreateShip.materialCount()
      Ok(views.html.statistics(sCounts.reverse))
    }
  }

  def cship(fuel: Int, ammo: Int, steel: Int, bauxite: Int, develop: Int) = Action.async {
    Future {
      val mat = Mat(fuel, ammo, steel, bauxite, develop)
      val cships = models.CreateShip.findAllByMatWithName(mat, limit = 100)
      val counts = models.CreateShip.countByMat(mat)
      val sum = counts.map(_._2).sum.toDouble
      val withRate = counts.map { case (ship, count) => (ship.name, count, count/sum) }
      val countJsonRaw = counts.map { case (ship, count) =>
        Map("label" -> ship.name, "data" -> count)
      }
      val title = s"$fuel/$ammo/$steel/$bauxite/$develop"
      Ok(views.html.cship(title, write(countJsonRaw), withRate, cships))
    }
  }
}
