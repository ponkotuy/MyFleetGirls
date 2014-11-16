package controllers

import models.db
import models.join.{ItemMat, Mat, ShipWithName}
import org.json4s._
import org.json4s.native.Serialization.write
import play.api.mvc._
import ranking.Ranking
import scalikejdbc._

import scala.util.Try

/**
 * Date: 14/06/11.
 */
object ViewSta extends Controller {
  import controllers.Common._

  def activities = actionAsync {
    Ok(views.html.sta.activities())
  }

  def statistics = actionAsync {
    val sCounts = db.CreateShip.materialCount().takeWhile(_._2 > 1)
    val iCounts = db.CreateItem.materialCount().takeWhile(_._2 > 1)
    Ok(views.html.sta.statistics(sCounts, iCounts))
  }

  def cship(fuel: Int, ammo: Int, steel: Int, bauxite: Int, develop: Int) = actionAsync {
    val mat = Mat(fuel, ammo, steel, bauxite, develop)
    val counts = db.CreateShip.countByMatWithMaster(mat)
    val title = s"$fuel/$ammo/$steel/$bauxite/$develop"
    val graphJson = cshipGraphJson(counts, title)
    val sum = counts.map(_._2).sum.toDouble
    val withRate = counts.map { case (ship, count) => (ship.name, count, count/sum) }
    val cships = db.CreateShip.findAllByMatWithName(mat, limit = 100)
    Ok(views.html.sta.cship(title, graphJson, withRate, cships))
  }

  private def cshipGraphJson(counts: List[(db.MasterShipBase, Long)], title: String): String = {
    implicit val formats: Formats = DefaultFormats
    val sum = counts.map(_._2).sum.toDouble
    val sTypeName = db.MasterStype.findAll().map(ms => ms.id -> ms.name).toMap
    val sTypeCounts = counts.groupBy(it => sTypeName(it._1.stype)).mapValues(_.map(_._2).sum)
    val data = sTypeCounts.map { case (sname, sCount) =>
      val countByShip = counts.filter { case (ship, _) => sTypeName(ship.stype) == sname }
      val children = countByShip.map { case (ship, count) =>
        Map("name" -> s"${ship.name} $count(${toP(count/sum)}%)", "count" -> count)
      }
      Map("name" -> s"${sname} $sCount(${toP(sCount/sum)}%)", "children" -> children)
    }
    write(Map("name" -> title, "children" -> data))
  }

  def citem(fuel: Int, ammo: Int, steel: Int, bauxite: Int, sType: String) = actionAsync {
    val mat = ItemMat(fuel, ammo, steel, bauxite, -1, sType)
    val citems = db.CreateItem.findAllByWithName(
      sqls"ci.fuel = $fuel and ci.ammo = $ammo and ci.steel = $steel and ci.bauxite = $bauxite and mst.name = $sType",
      limit = 100
    )
    val counts = db.CreateItem.countItemByMat(mat)
    val sum = counts.map(_._2).sum.toDouble
    val withRate = counts.map { case (item, count) => (item.name, count, count/sum) }
    val countJsonRaw = counts.map { case (item, count) =>
      val url = routes.ViewSta.fromShip().toString() + s"#query=${item.name}"
      Map("label" -> item.name, "data" -> count, "url" -> url)
    }
    val title = s"${sType}/$fuel/$ammo/$steel/$bauxite"
    Ok(views.html.sta.citem(title, write(countJsonRaw), withRate, citems))
  }

  def fromShip() = actionAsync { Ok(views.html.sta.from_ship()) }

  def dropStage() = actionAsync {
    val stages = db.BattleResult.countAllByStage()
    Ok(views.html.sta.drop_stage(stages))
  }

  def drop(area: Int, info: Int) = actionAsync {
    val cells = db.BattleResult.dropedCells(area, info)
    Ok(views.html.sta.drop(area, info, cells))
  }

  def dropAlpha(area: Int, info: Int) = actionAsync {
    val cells = db.BattleResult.dropedCellsAlpha(area, info)
    Ok(views.html.sta.drop_alpha(area, info, cells))
  }

  def route(area: Int, info: Int) = actionAsync { Ok(views.html.sta.route(area, info)) }

  def routeFleet(area: Int, info: Int, dep: Int, dest: Int) = actionAsync {
    val fleets = db.MapRoute.findFleetBy(sqls"area_id = $area and info_no = $info and dep = $dep and dest = $dest")
    val counts = fleetCounts(fleets)
    val cDep = db.CellInfo.findOrDefault(area, info, dep)
    val cDest = db.CellInfo.findOrDefault(area, info, dest)
    Ok(views.html.sta.modal_route(area, info, cDep, cDest, counts))
  }

  private def fleetCounts(fleets: Seq[Seq[ShipWithName]]): Seq[(Seq[String], Int)] = {
    fleets.map { xs => xs.map(_.stype.name).sorted }
      .groupBy(identity).mapValues(_.size)
      .toList.sortBy(_._2).reverse.take(30)
  }

  def ranking() = actionAsync { Ok(views.html.sta.ranking(Ranking.values)) }

  def rankingDetails(_ranking: String) = actionAsync {
    Ranking.fromString(_ranking).map { ranking =>
      Ok(views.html.sta.modal_ranking(ranking))
    }.getOrElse(NotFound("そのようなRankingは見つかりません"))
  }

  val StaBookURL = "/entire/sta/book/"
  def shipList() = actionAsync {
    val ships = db.MasterShipBase.findAllWithStype(sqls"ms.sortno > 0")
    Ok(views.html.sta.ship_list(ships, favCountTableByShip()))
  }

  def favCountTableByShip(): Map[Int, Long] = {
    val favs = db.Favorite.countByURL(sqls"f.first = ${"entire"} and f.second = ${"sta"} and f.url like ${StaBookURL + "%"}")
    favs.flatMap { case (url, _, count) =>
      Try { url.replace(StaBookURL, "").toInt }.map(_ -> count).toOption
    }.toMap.withDefaultValue(0L)
  }

  def shipBook(sid: Int) = actionAsync {
    db.MasterShipBase.findAllInOneBy(sqls"ms.id = $sid").headOption.map { master =>
      val ships = db.Ship.findByWithAdmiral(sid)
      val admiral = db.ShipImage.findAdmiral(sid)
      Ok(views.html.sta.ship_book(master, ships, admiral))
    }.getOrElse(NotFound(s"Not Found ShipID: $sid"))
  }

  def remodelSlot() = actionAsync {
    val ids = db.RemodelSlot.findAllUniqueSlotId()
    val slots = db.MasterSlotItem.findIn(ids)
    Ok(views.html.sta.remodel_slot(slots))
  }

  private def toP(d: Double): String = f"${d*100}%.1f"
}
