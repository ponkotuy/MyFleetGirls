package controllers

import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import org.json4s._
import org.json4s.native.Serialization.write
import scalikejdbc.SQLInterpolation._
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
      val newest = models.Admiral.findNewest(limit = 20)
      val lvTops = models.Admiral.findAllLvTop(limit = 20)
      Ok(views.html.index(BuildInfo.version, newest, lvTops))
    }
  }

  def about = Action { Ok(views.html.about()) }

  def statistics = Action.async {
    Future {
      val sCounts = models.CreateShip.materialCount().takeWhile(_._2 > 1)
      val iCounts = models.CreateItem.materialCount().takeWhile(_._2 > 1)
      Ok(views.html.sta.statistics(sCounts, iCounts))
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
      Ok(views.html.sta.cship(title, write(countJsonRaw), withRate, cships))
    }
  }

  def citem(fuel: Int, ammo: Int, steel: Int, bauxite: Int, sType: Int) = Action.async {
    Future {
      val mat = models.ItemMat(fuel, ammo, steel, bauxite, sType, "")
      val citems = models.CreateItem.findAllByWithName(
        sqls"ci.fuel = $fuel and ci.ammo = $ammo and ci.steel = $steel and ci.bauxite = $bauxite and ms.stype = $sType",
        limit = 100
      )
      val counts = models.CreateItem.countItemByMat(mat)
      val sum = counts.map(_._2).sum.toDouble
      val withRate = counts.map { case (item, count) => (item.name, count, count/sum) }
      val countJsonRaw = counts.map { case (item, count) =>
        Map("label" -> item.name, "data" -> count)
      }
      val st = models.MasterStype.find(sType).get
      val title = s"${st.name}/$fuel/$ammo/$steel/$bauxite"
      Ok(views.html.sta.citem(title, write(countJsonRaw), withRate, citems))
    }
  }
  def fromShip(q: String) = Action.async {
    Future(Ok(views.html.sta.from_ship(q)))
  }
}
