package controllers

import play.api.mvc.Controller
import scalikejdbc._
import org.json4s._
import org.json4s.JsonDSL._
import dat.ShipDrop

/**
 *
 * @author ponkotuy
 * Date: 14/02/23
 */
object Rest extends Controller {
  import Common._

  def searchUser(q: String) = returnJson(models.Admiral.findAllByLike(s"%$q%", limit = 20))

  /** Createされた記録のあるMasterShipとMasterSlotItemを検索 */
  def searchMaster(q: String) = returnJson {
    val ships = models.CreateShip.findAllShipByNameLike(s"%$q%") ++
      models.BattleResult.findAllShipByNameLike(s"%$q%")
    val items = models.CreateItem.findAllItemByNameLike(sqls"mi.name like ${s"%$q%"}")
    Map("ships" -> ships.distinct, "items" -> items)
  }

  /** Createされた記録のあるMasterShipを検索 */
  def searchMasterShip(q: String) = returnJson {
    models.CreateShip.findAllShipByNameLike(s"%$q%") ++
      models.BattleResult.findAllShipByNameLike(s"%$q%")
  }

  def recipeFromShip(shipId: Int) = returnJson {
    val allCounts = models.CreateShip.materialCount().toMap
    val counts = models.CreateShip.materialCount(sqls"result_ship = ${shipId}")
    counts.map { case (mat, count) =>
      Map("mat" -> mat, "count" -> count, "sum" -> allCounts(mat))
    }
  }

  def dropFromShip(shipId: Int) = returnJson {
    val allCounts = models.BattleResult.countAllGroupByCells().toMap
    val dropCounts = models.BattleResult.countAllGroupByCells(sqls"get_ship_id = ${shipId}")
    dropCounts.map { case (cell, count) =>
      Map("cell" -> cell, "count" -> count, "sum" -> allCounts(cell))
    }
  }

  def recipeFromItem(itemId: Int) = returnJson {
    val allCounts = models.CreateItem.materialCount().toMap
    val counts = models.CreateItem.materialCount(sqls"slotitem_id = ${itemId}")
    counts.map { case (mat, count) =>
      Map("mat" -> mat, "count" -> count, "sum" -> allCounts(mat))
    }
  }

  def dropCell(area: Int, info: Int, cell: Int, rank: String) = returnJson {
    val drops = models.BattleResult.countCellsGroupByDrop(area, info, cell, rank)
    val sum = drops.map(_._2).sum.toDouble
    drops.map(dropToJson(sum))
  }

  def dropCellAlpha(area: Int, info: Int, alpha: String, rank: String) = returnJson {
    val drops = models.BattleResult.countCellsAlphaGroupByDrop(area, info, alpha, rank)
    val sum = drops.map(_._2).sum.toDouble
    drops.map(dropToJson(sum))
  }

  private def dropToJson(sum: Double)(sCount: (ShipDrop, Long)) = {
    val (drop, count) = sCount
    Extraction.decompose(drop).asInstanceOf[JObject] ~
      ("count" -> count) ~
      ("rate" -> f"${count / sum * 100}%.1f%%")
  }

  def route(area: Int, info: Int) = returnJson {
    val routes = models.MapRoute.countCellsGroupByDest(area, info)
    routes.map { case (route, count) =>
      Extraction.decompose(route).asInstanceOf[JObject] ~ ("count" -> count)
    }
  }

  def cellInfo(area: Int, info: Int) = returnJson {
    val where = sqls"true"
      .append(if(area != -1) sqls" and area_id = ${area}" else sqls"")
      .append(if(info != -1) sqls" and info_no = ${info}" else sqls"")
    models.CellInfo.findAllBy(where)
  }

  def maps() = returnJson {
    models.MapRoute.findStageUnique()
  }

}
