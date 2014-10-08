package controllers

import dat.ShipWithName
import org.json4s._
import org.json4s.native.Serialization.write
import play.api.mvc._
import ranking.Ranking
import scalikejdbc._

/**
 * Date: 14/06/11.
 */
object ViewSta extends Controller {
  import controllers.Common._

  def activities = actionAsync {
    Ok(views.html.sta.activities())
  }

  def statistics = actionAsync {
    val sCounts = models.CreateShip.materialCount().takeWhile(_._2 > 1)
    val iCounts = models.CreateItem.materialCount().takeWhile(_._2 > 1)
    Ok(views.html.sta.statistics(sCounts, iCounts))
  }

  def cship(fuel: Int, ammo: Int, steel: Int, bauxite: Int, develop: Int) = actionAsync {
    val mat = dat.Mat(fuel, ammo, steel, bauxite, develop)
    val counts = models.CreateShip.countByMatWithMaster(mat)
    val title = s"$fuel/$ammo/$steel/$bauxite/$develop"
    val graphJson = cshipGraphJson(counts, title)
    val sum = counts.map(_._2).sum.toDouble
    val withRate = counts.map { case (ship, count) => (ship.name, count, count/sum) }
    val cships = models.CreateShip.findAllByMatWithName(mat, limit = 100)
    Ok(views.html.sta.cship(title, graphJson, withRate, cships))
  }

  private def cshipGraphJson(counts: List[(models.MasterShipBase, Long)], title: String): String = {
    implicit val formats: Formats = DefaultFormats
    val sum = counts.map(_._2).sum.toDouble
    val sTypeName = models.MasterStype.findAll().map(ms => ms.id -> ms.name).toMap
    val className = models.MasterShipBase.findAllWithClass().map(msb => msb.ctype -> msb.cls).toMap
    val sTypeCounts = counts.groupBy(it => sTypeName(it._1.stype)).mapValues(_.map(_._2).sum)
    val data = sTypeCounts.map { case (sname, sCount) =>
      val classes = counts.filter(it => sTypeName(it._1.stype) == sname)
      val classCounts = classes.groupBy(_._1.ctype).mapValues(_.map(_._2).sum)
      val children = classCounts.map { case (ctype, cCount) =>
        val children = counts.filter(_._1.ctype == ctype).map { case (msb, count) =>
          Map("name" -> s"${msb.name} $count(${toP(count/sum)}%)", "count" -> count)
        }
        Map("name" -> s"${className(ctype)} $cCount(${toP(cCount/sum)}%)", "children" -> children)
      }
      Map("name" -> s"${sname} $sCount(${toP(sCount/sum)}%)", "children" -> children)
    }
    write(Map("name" -> title, "children" -> data))
  }

  def citem(fuel: Int, ammo: Int, steel: Int, bauxite: Int, sType: String) = actionAsync {
    val mat = dat.ItemMat(fuel, ammo, steel, bauxite, -1, sType)
    val citems = models.CreateItem.findAllByWithName(
      sqls"ci.fuel = $fuel and ci.ammo = $ammo and ci.steel = $steel and ci.bauxite = $bauxite and mst.name = $sType",
      limit = 100
    )
    val counts = models.CreateItem.countItemByMat(mat)
    val sum = counts.map(_._2).sum.toDouble
    val withRate = counts.map { case (item, count) => (item.name, count, count/sum) }
    val countJsonRaw = counts.map { case (item, count) =>
      Map("label" -> item.name, "data" -> count)
    }
    val title = s"${sType}/$fuel/$ammo/$steel/$bauxite"
    Ok(views.html.sta.citem(title, write(countJsonRaw), withRate, citems))
  }

  def fromShip() = actionAsync { Ok(views.html.sta.from_ship()) }

  def dropStage() = actionAsync {
    val stages = models.BattleResult.countAllByStage()
    Ok(views.html.sta.drop_stage(stages))
  }

  def drop(area: Int, info: Int) = actionAsync {
    val cells = models.BattleResult.dropedCells(area, info)
    Ok(views.html.sta.drop(area, info, cells))
  }

  def dropAlpha(area: Int, info: Int) = actionAsync {
    val cells = models.BattleResult.dropedCellsAlpha(area, info)
    Ok(views.html.sta.drop_alpha(area, info, cells))
  }

  def route(area: Int, info: Int) = actionAsync { Ok(views.html.sta.route(area, info)) }

  def routeFleet(area: Int, info: Int, dep: Int, dest: Int) = actionAsync {
    val fleets = models.MapRoute.findFleetBy(sqls"area_id = $area and info_no = $info and dep = $dep and dest = $dest")
    val counts = fleetCounts(fleets)
    val cDep = models.CellInfo.findOrDefault(area, info, dep)
    val cDest = models.CellInfo.findOrDefault(area, info, dest)
    Ok(views.html.sta.modal_route(area, info, cDep, cDest, counts))
  }

  private def fleetCounts(fleets: Seq[Seq[ShipWithName]]): Seq[(Seq[String], Int)] = {
    fleets.map { xs => xs.map(_.stype.name).sorted }
      .groupBy(identity).mapValues(_.size)
      .toList.sortBy(_._2).reverse.take(30)
  }

  def ranking() = actionAsync {
    Ok(views.html.sta.ranking(Ranking.values))
/*
    val itemBook = models.AdmiralRanking.findAllOrderByItemBookCount(20, agoMillis(30.days))
    Ok(views.html.sta.ranking(materials, yomeLv, firstLv, bookCounts, married, shipExp, expByShip, itemBook))
    */
  }

  private def toP(d: Double): String = f"${d*100}%.1f"
}
