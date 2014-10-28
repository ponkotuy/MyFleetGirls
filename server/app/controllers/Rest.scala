package controllers

import dat.ShipDrop
import org.json4s.JsonDSL._
import org.json4s._
import play.api.mvc.Controller
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/02/23
 */
object Rest extends Controller {
  import controllers.Common._

  def searchUser(q: String) = returnJson(models.Admiral.findAllByLike(s"%$q%", limit = 20))

  def searchBaseUser(serverId: Int) = returnJson(models.Admiral.findAllByServer(serverId, limit = 20))

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
      Map("mat" -> mat, "count" -> count, "sum" -> allCounts.lift(mat).getOrElse(Long.MaxValue))
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

  val NGStage = Set((1, 1), (2, 2), (2, 3))
  def activities(from: Long, limit: Int, offset: Int) = returnJson {
    val started = models.MapRoute.findWithUserBy(sqls"mr.created > ${from}", limit*8, offset)
      .filter(_.start.exists(_.start))
      .filterNot { it => NGStage.contains((it.areaId, it.infoNo)) }
    val rares = models.MasterShipOther.findAllBy(sqls"mso.backs >= 5").map(_.id).toSet
    val drops = models.BattleResult.findWithUserBy(sqls"br.created > ${from} and br.get_ship_id is not null", limit*8, offset)
      .filter(_.getShipId.exists(rares.contains))
    val rareItems = models.MasterSlotItem.findAllBy(sqls"msi.rare >= 1").map(_.id).toSet
    val createItems = models.CreateItem.findWithUserBy(sqls"ci.created > ${from}", limit, offset)
      .filter { it => rareItems.contains(it.itemId) }
    val createShips = models.CreateShip.findWithUserBy(sqls"cs.created > ${from}", limit, offset)
      .filter { it => rares.contains(it.shipId) }
    (started ++ drops ++ createItems ++ createShips)
      .sortBy(_.created).reverse.take(limit).map(_.toJSON)
  }

  def remodelLog(slotId: Int) = returnJson {
    models.RemodelSlot.findAllWithSecondShipBy(sqls"r.slot_id = ${slotId}").sortBy(-_.remodel.created)
  }
}
