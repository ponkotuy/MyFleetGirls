package controllers

import play.api.mvc.Controller
import scalikejdbc.SQLInterpolation._
import org.json4s._
import org.json4s.JsonDSL._
import com.ponkotuy.data.GetShip

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
    val ships = models.CreateShip.findAllShipByNameLike(s"%$q%")
    val items = models.CreateItem.findAllItemByNameLike(sqls"mi.name like ${s"%$q%"}")
    Map("ships" -> ships, "items" -> items)
  }

  /** Createされた記録のあるMasterShipを検索 */
  def searchMasterShip(q: String) = returnJson(models.CreateShip.findAllShipByNameLike(s"%$q%"))

  def recipeFromShip(shipId: Int) = returnJson {
    val allCounts = models.CreateShip.materialCount().toMap
    val counts = models.CreateShip.materialCount(sqls"result_ship = ${shipId}")
    counts.map { case (mat, count) =>
      Map("mat" -> mat, "count" -> count, "sum" -> allCounts(mat))
    }
  }

  def recipeFromItem(itemId: Int) = returnJson {
    val allCounts = models.CreateItem.materialCount().toMap
    val counts = models.CreateItem.materialCount(sqls"slotitem_id = ${itemId}")
    counts.map { case (mat, count) =>
      Map("mat" -> mat, "count" -> count, "sum" -> allCounts(mat))
    }
  }

  def drop(area: Int, info: Int, rank: String) = returnJson {
    val drops = models.BattleResult.countAllGroupByDrop(area, info, rank)
    val raw: Map[(Int, Int, Int), List[(Option[GetShip], Long, String)]] =
      drops.groupBy(_._1.point).mapValues { xs =>
        val sum = xs.map(_._2).sum.toDouble
        xs.map { case (drop, count) =>
          val rate = f"${count / sum * 100}%.1f%%"
          (drop.getShip, count, rate)
        }.reverse
      }
    raw.toSeq.sortBy(_._1).map { case ((a, i, c), rest) =>
      val ci = models.CellInfo.find(a, i, c)
        .getOrElse(models.CellInfo.noAlphabet(a, i, c))
      val ships = rest.map { case (ship, count, rate) =>
        val shipJson = ship.map(Extraction.decompose).orNull
        ("ship" -> shipJson) ~ ("count" -> count) ~ ("rate" -> rate)
      }
      Extraction.decompose(ci).asInstanceOf[JObject] ~ ("getship" -> ships)
    }
  }

  def dropCell(area: Int, info: Int, cell: Int, rank: String) = returnJson {
    val drops = models.BattleResult.countCellsGroupByDrop(area, info, cell, rank)
    val sum = drops.map(_._2).sum.toDouble
    drops.map { case (drop, count) =>
      Extraction.decompose(drop).asInstanceOf[JObject] ~
        ("count" -> count) ~
        ("rate" -> f"${count / sum * 100}%.1f%%")
    }
  }

  def materials(userId: Long) = returnJson(models.Material.findAllByUser(userId))

  def basics(userId: Long) = returnJson(models.Basic.findAllByUser(userId))

  def ndocks(memberId: Long) = returnJson(models.NDock.findAllByUserWithName(memberId))

  def kdocks(memberId: Long) = returnJson(models.KDock.findAllByUserWithName(memberId))

  def missions(memberId: Long) = returnJson(models.Mission.findByUserWithFlagship(memberId))

  def createShips(memberId: Long, limit: Int, offset: Int, large: Boolean) = returnJson {
    models.CreateShip.findAllByUserWithName(memberId, large, limit, offset)
  }

  def createShipCount(memberId: Long, large: Boolean) =
    returnString(models.CreateShip.countByUser(memberId, large))

  def createItems(memberId: Long, limit: Int, offset: Int) = returnJson {
    models.CreateItem.findAllByWithName(sqls"ci.member_id = ${memberId}", limit, offset)
  }

  def createItemCount(memberId: Long) = returnString(models.CreateItem.countBy(sqls"member_id = ${memberId}"))
}
