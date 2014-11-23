package controllers

import models.db
import models.join.{ShipDrop, ShipWithFav}
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import org.json4s.JsonDSL._
import org.json4s._
import play.api.mvc.Controller
import scalikejdbc._

import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/02/23
 */
object Rest extends Controller {
  import controllers.Common._

  lazy val DefaultStart = new LocalDate(2014, 1, 1)

  def searchUser(q: String) = returnJson(db.Admiral.findAllByLike(s"%$q%", limit = 20))

  def searchBaseUser(serverId: Int) = returnJson(db.Admiral.findAllByServer(serverId, limit = 20))

  def searchBaseUser(serverId: Int, name: String) = returnJson {
    val where = sqls"a.nickname like ${s"%$name%"}"
    db.Admiral.findAllByServer(serverId, where = where, limit = 20)
  }

  /** Createされた記録のあるMasterShipとMasterSlotItemを検索 */
  def searchMaster(q: String) = returnJson {
    val ships = db.CreateShip.findAllShipByNameLike(s"%$q%") ++
      db.BattleResult.findAllShipByNameLike(s"%$q%")
    val items = db.CreateItem.findAllItemByNameLike(sqls"mi.name like ${s"%$q%"}")
    Map("ships" -> ships.distinct, "items" -> items)
  }

  /** Createされた記録のあるMasterShipを検索 */
  def searchMasterShip(q: String) = returnJson {
    db.CreateShip.findAllShipByNameLike(s"%$q%") ++
      db.BattleResult.findAllShipByNameLike(s"%$q%")
  }

  def recipeFromShip(shipId: Int, from: String, to: String) = returnJson {
    val fromTo = whereFromTo(sqls"cs.created", from, to)
    val allCounts = db.CreateShip.materialCount(fromTo).toMap
    val counts = db.CreateShip.materialCount(sqls"result_ship = ${shipId} and ${fromTo}")
    counts.map { case (mat, count) =>
      Map("mat" -> mat, "count" -> count, "sum" -> allCounts(mat))
    }
  }

  def dropFromShip(shipId: Int, from: String, to: String) = returnJson {
    val fromTo = whereFromTo(sqls"br.created", from, to)
    val allCounts = db.BattleResult.countAllGroupByCells(fromTo).toMap
    val dropCounts = db.BattleResult.countAllGroupByCells(sqls"get_ship_id = ${shipId} and $fromTo")
    dropCounts.map { case (cell, count) =>
      Map("cell" -> cell, "count" -> count, "sum" -> allCounts(cell))
    }
  }

  private def whereFromTo(target: SQLSyntax, from: String, to: String): SQLSyntax = {
    val fromDate = Try { LocalDate.parse(from, ISODateTimeFormat.date()) }.getOrElse(DefaultStart)
    val toDate = Try { LocalDate.parse(to, ISODateTimeFormat.date()) }.getOrElse(LocalDate.now())
    sqls"${fromDate.toDate.getTime} < $target and $target < ${toDate.toDate.getTime}"
  }

  def recipeFromItem(itemId: Int, from: String, to: String) = returnJson {
    val fromTo = whereFromTo(sqls"ci.created", from, to)
    val allCounts = db.CreateItem.materialCount(fromTo).toMap
    val counts = db.CreateItem.materialCount(sqls"slotitem_id = ${itemId} and ${fromTo}")
    counts.map { case (mat, count) =>
      Map("mat" -> mat, "count" -> count, "sum" -> allCounts.lift(mat).getOrElse(Long.MaxValue))
    }
  }

  def dropCell(area: Int, info: Int, cell: Int, rank: String) = returnJson {
    val drops = db.BattleResult.countCellsGroupByDrop(area, info, cell, rank)
    val sum = drops.map(_._2).sum.toDouble
    drops.map(dropToJson(sum))
  }

  def dropCellAlpha(area: Int, info: Int, alpha: String, rank: String) = returnJson {
    val drops = db.BattleResult.countCellsAlphaGroupByDrop(area, info, alpha, rank)
    val sum = drops.map(_._2).sum.toDouble
    drops.map(dropToJson(sum))
  }

  private def dropToJson(sum: Double)(sCount: (ShipDrop, Long)) = {
    val (drop, count) = sCount
    Extraction.decompose(drop).asInstanceOf[JObject] ~
      ("count" -> count) ~
      ("sum" -> sum) ~
      ("rate" -> f"${count / sum * 100}%.1f%%")
  }

  def route(area: Int, info: Int) = returnJson {
    val routes = db.MapRoute.countCellsGroupByDest(area, info)
    routes.map { case (route, count) =>
      Extraction.decompose(route).asInstanceOf[JObject] ~ ("count" -> count)
    }
  }

  def cellInfo(area: Int, info: Int) = returnJson {
    val where = sqls"true"
      .append(if(area != -1) sqls" and area_id = ${area}" else sqls"")
      .append(if(info != -1) sqls" and info_no = ${info}" else sqls"")
    db.CellInfo.findAllBy(where)
  }

  def maps() = returnJson {
    db.MapRoute.findStageUnique()
  }

  val NGStage = Set((1, 1), (2, 2), (2, 3))
  def activities(from: Long, limit: Int, offset: Int) = returnJson {
    val started = db.MapRoute.findWithUserBy(sqls"mr.created > ${from}", limit*8, offset)
      .filter(_.start.exists(_.start))
      .filterNot { it => NGStage.contains((it.areaId, it.infoNo)) }
    val rares = db.MasterShipOther.findAllBy(sqls"mso.backs >= 5").map(_.id).toSet
    val drops = db.BattleResult.findWithUserBy(sqls"br.created > ${from} and br.get_ship_id is not null", limit*8, offset)
      .filter(_.getShipId.exists(rares.contains))
    val rareItems = db.MasterSlotItem.findAllBy(sqls"msi.rare >= 1").map(_.id).toSet
    val createItems = db.CreateItem.findWithUserBy(sqls"ci.created > ${from}", limit, offset)
      .filter { it => rareItems.contains(it.itemId) }
    val createShips = db.CreateShip.findWithUserBy(sqls"cs.created > ${from}", limit, offset)
      .filter { it => rares.contains(it.shipId) }
    (started ++ drops ++ createItems ++ createShips)
      .sortBy(_.created).reverse.take(limit).map(_.toJSON)
  }

  def bookShips(q: String) = returnJson {
    val ships = db.MasterShipBase.findAllWithStype(sqls"ms.sortno > 0 and ms.name like ${s"%$q%"}")
    val favCounts = ViewSta.favCountTableByShip()
    ships.groupBy(_.stypeName).toVector.sortBy(-_._2.length)
      .map { case (stype, xs) =>
        ("stype", stype) ~
          ("ships", xs.map(ShipWithFav.fromWithStype(_, favCounts).toJson))
      }
  }

  def remodelLog(slotId: Int) = returnJson {
    db.RemodelSlot.findAllWithSecondShipBy(sqls"r.slot_id = ${slotId}").sortBy(-_.remodel.created)
  }
}
