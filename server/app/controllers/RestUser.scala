package controllers

import org.json4s.JsonDSL._
import org.json4s._
import play.api.mvc.Controller
import scalikejdbc._

/**
 * Date: 14/06/12.
 */
object RestUser extends Controller {
  import controllers.Common._

  def materials(userId: Long) = returnJson(models.Material.findAllByUser(userId))

  def basics(userId: Long) = returnJson(models.Basic.findAllByUser(userId))

  def ndocks(memberId: Long) = returnJson(models.NDock.findAllByUserWithName(memberId))

  def kdocks(memberId: Long) = returnJson(models.KDock.findAllByUserWithName(memberId))

  def missions(memberId: Long) = returnJson(models.Mission.findByUserWithFlagship(memberId))

  def conds(memberId: Long) = returnJson {
    val ships = models.Ship.findAllByUserWithName(memberId)
    val now = System.currentTimeMillis()
    for {
      ship <- ships
      cond = ship.cond + ((now - ship.created) / (3L * 60 * 1000) * 3).toInt
      if cond < 49
    } yield {
      Map[String, Any](
        "id" -> ship.id, "name" -> ship.name, "stype" -> ship.stName, "cond" -> cond, "rest" -> (49 - cond)
      )
    }
  }

  def createShips(memberId: Long, limit: Int, offset: Int, large: Boolean) = returnJson {
    models.CreateShip.findAllByUserWithName(memberId, large, limit, offset)
  }

  def createShipCount(memberId: Long, large: Boolean) =
    returnString(models.CreateShip.countByUser(memberId, large))

  def createItems(memberId: Long, limit: Int, offset: Int) = returnJson {
    models.CreateItem.findAllByWithName(sqls"ci.member_id = ${memberId}", limit, offset)
  }

  def createItemCount(memberId: Long) = returnString(models.CreateItem.countBy(sqls"member_id = ${memberId}"))

  def battleResult(memberId: Long, limit: Int, offset: Int, boss: Boolean, drop: Boolean, rank: String) = returnJson {
    val where = battleResultWhere(memberId, boss, drop, rank)
    val result = models.BattleResult.findAllByWithCell(where, limit, offset)
    JArray(result.map(_.toJson))
  }

  def battleResultCount(memberId: Long, boss: Boolean, drop: Boolean, rank: String) = returnString {
    val where = battleResultWhere(memberId, boss, drop, rank)
    models.BattleResult.countBy(where)
  }

  private def battleResultWhere(memberId: Long, boss: Boolean, drop: Boolean, rank: String) =
    sqls"member_id = ${memberId}"
      .append(if(rank.nonEmpty) sqls" and win_rank in (${rank.map(_.toString)})" else sqls"")
      .append(if(boss) sqls" and boss = true" else sqls"")
      .append(if(drop) sqls" and get_ship_id is not null" else sqls"")

  def routeLog(memberId: Long, limit: Int, offset: Int, area: Int, info: Int) = returnJson {
    val result = models.MapRoute.findAllBy(routeLogWhere(memberId, area, info), limit, offset)
    val ships = models.Ship.findAllByUserWithName(memberId)
      .map(s => s.id -> s).toMap
    result.map { route =>
      val xs = route.fleet.map { sid =>
        ships.get(sid).map { ship =>
          ("id" -> ship.id) ~ ("name" -> ship.name) ~ ("stype" -> ship.stName)
        }.getOrElse {
          ("id" -> sid) ~ ("name" -> "轟沈艦") ~ ("stype" -> "不明")
        }
      }
      Extraction.decompose(route).asInstanceOf[JObject] ~ ("ships" -> xs)
    }
  }

  def routeLogCount(memberId: Long, area: Int, info: Int) = returnString {
    models.MapRoute.countBy(routeLogWhere(memberId, area, info))
  }

  private def routeLogWhere(memberId: Long, area: Int, info: Int) = {
    sqls"member_id = ${memberId}"
      .append(if(area != -1) sqls" and area_id = ${area}" else sqls"")
      .append(if(info != -1) sqls" and info_no = ${info}" else sqls"")
  }

  def quest(memberId: Long) = returnJson { models.Quest.findAllBy(sqls"member_id = ${memberId}") }
}
