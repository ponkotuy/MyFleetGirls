package controllers

import honor.Honors
import models.db
import models.other.ShipWithCondition
import models.req.{AllCrawlAPI, SortType}
import org.json4s.JsonDSL._
import org.json4s._
import play.api.mvc.Controller
import scalikejdbc._

/**
 * Date: 14/06/12.
 */
object RestUser extends Controller {
  import controllers.Common._

  def materials(userId: Long) = returnJson(db.Material.findAllByUser(userId))

  def basics(userId: Long) = returnJson(db.Basic.findAllByUser(userId))

  def scores(memberId: Long) = returnJson(db.Ranking.findAllBy(sqls.eq(db.Ranking.r.memberId, memberId)))

  def ndocks(memberId: Long) = returnJson(db.NDock.findAllByUserWithName(memberId))

  def kdocks(memberId: Long) = returnJson(db.KDock.findAllByUserWithName(memberId))

  def missions(memberId: Long) = returnJson(db.Mission.findByUserWithFlagship(memberId))

  def conds(memberId: Long) = returnJson {
    val ships = db.Ship.findAllByUserWithName(memberId)
    ships.map(ShipWithCondition.fromShip).filter(_.cond < 49)
  }

  def createShips(memberId: Long, limit: Int, offset: Int, large: Boolean) = returnJson {
    require(limit + offset <= 100, "limit + offset <= 100")
    db.CreateShip.findAllByUserWithName(memberId, large, limit, offset)
  }

  def createShipCount(memberId: Long, large: Boolean) =
    returnString(db.CreateShip.countByUser(memberId, large))

  def createItems(memberId: Long, limit: Int, offset: Int) = returnJson {
    require(limit + offset <= 100, "limit + offset <= 100")
    db.CreateItem.findAllByWithName(sqls"ci.member_id = ${memberId}", limit, offset)
  }

  def createItemCount(memberId: Long) = returnString(db.CreateItem.countBy(sqls"member_id = ${memberId}"))

  def remodels(memberId: Long, limit: Int, offset: Int) = returnJson {
    require(limit + offset <= 100, "limit + offset <= 100")
    db.Remodel.findAllByWithName(sqls"r.member_id = ${memberId}", limit, offset)
  }

  def remodelCount(memberId: Long) = returnString(db.Remodel.countBy(sqls"r.member_id = ${memberId}"))

  def battleResult(memberId: Long, limit: Int, offset: Int, boss: Boolean, drop: Boolean, rank: String, area: Option[Int], info: Option[Int]) = returnJsonReq[JValue] { implicit req =>
    AllCrawlAPI.form.bindFromRequest().get match {
      case AllCrawlAPI(SortType.Normal, _) =>
        require(limit + offset <= 200, "limit + offset <= 200")
        val where = battleResultWhere(memberId, boss, drop, rank, area, info)
        val result = db.BattleResult.findAllByWithCell(where, limit, offset)
        JArray(result.map(_.toJson))
      case AllCrawlAPI(SortType.Id, fromId) =>
        val where = sqls"member_id = ${memberId}".and.append(fromId.map { id => sqls"id > ${id}" }.getOrElse(sqls"1"))
        val result = db.BattleResult.findAllByWithCell(where, 100, orderBy = sqls"br.id")
        JArray(result.map(_.toJson))
    }
  }

  def battleResultCount(memberId: Long, boss: Boolean, drop: Boolean, rank: String, area: Option[Int], info: Option[Int]) = returnString {
    val where = battleResultWhere(memberId, boss, drop, rank, area, info)
    if(boss) db.BattleResult.countByWithCellInfo(where)
    else db.BattleResult.countBy(where)
  }

  private def battleResultWhere(memberId: Long, boss: Boolean, drop: Boolean, rank: String, area: Option[Int], info: Option[Int]) =
    sqls"member_id = ${memberId}"
      .append(if(rank.nonEmpty) sqls" and win_rank in (${rank.map(_.toString)})" else sqls"")
      .append(if(boss) sqls" and boss = true" else sqls"")
      .append(if(drop) sqls" and get_ship_id is not null" else sqls"")
      .append(area.map(a => sqls" and br.area_id = ${a}").getOrElse(sqls""))
      .append(info.map(i => sqls" and br.info_no = ${i}").getOrElse(sqls""))

  def routeLog(memberId: Long, limit: Int, offset: Int, area: Int, info: Int) = returnJson {
    require(limit + offset <= 210, "limit + offset <= 210")
    val result = db.MapRoute.findAllBy(routeLogWhere(memberId, area, info), limit, offset)
    val ships = db.Ship.findAllByUserWithName(memberId)
      .map(s => s.id -> s).toMap
    result.map { route =>
      val xs = route.fleet.map { sid =>
        ships.get(sid).map { ship =>
          ("id" -> ship.id) ~ ("name" -> ship.name) ~ ("stype" -> ship.stName)
        }.getOrElse {
          ("id" -> sid) ~ ("name" -> "喪失") ~ ("stype" -> "不明")
        }
      }
      route.toJson ~ ("ships" -> xs)
    }
  }

  def routeLogCount(memberId: Long, area: Int, info: Int) = returnString {
    db.MapRoute.countBy(routeLogWhere(memberId, area, info))
  }

  private def routeLogWhere(memberId: Long, area: Int, info: Int) = {
    sqls"member_id = ${memberId}"
      .append(if(area != -1) sqls" and area_id = ${area}" else sqls"")
      .append(if(info != -1) sqls" and info_no = ${info}" else sqls"")
  }

  def mission(memberId: Long, limit: Int, offset: Int, missionId: Option[Int]) = returnJson {
    require(limit + offset <= 200, "limit + offset <= 200")
    val where = missionWhere(memberId, missionId)
    val missions = db.MissionHistory.findAllByWithMaster(where, limit, offset)
      .sortBy(-_.completeTime)
    if(missions.isEmpty) throw new RuntimeException("Not found mission")
    val ships = db.MissionHistoryShip.findAllWithMasterShipBy(sqls"mhs.mission_id in (${missions.map(_.missionId)})")
    missions.map { m => m.toJsonWithShip(ships) }
  }

  def missionCount(memberId: Long, missionId: Option[Int]) = returnString {
    val where = missionWhere(memberId, missionId)
    db.MissionHistory.countBy(where)
  }

  private def missionWhere(memberId: Long, missionId: Option[Int]): SQLSyntax =
    sqls"mh.member_id = ${memberId}"
      .append(missionId.map(id => sqls" and mh.number = ${id}").getOrElse(sqls""))

  def quest(memberId: Long) = returnJson { db.Quest.findAllBy(sqls"member_id = ${memberId}") }

  def snap(memberId: Long, snapId: Long) = returnJson {
    db.DeckSnapshot.find(snapId).filter(_.memberId == memberId).get
  }

  def bookShips(memberId: Long) = returnJson {
    db.ShipBook.findAllBy(sqls"sb.member_id = ${memberId} and sb.index_no < 100000")
  }

  def honors(memberId: Long, set: Boolean) = returnJson {
    Honors.fromUser(memberId, set)
  }
}
