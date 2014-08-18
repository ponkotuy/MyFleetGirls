package com.ponkotuy.parser

import com.ponkotuy.data
import com.ponkotuy.data._
import com.ponkotuy.http.MFGHttp
import org.json4s._
import org.json4s.native.Serialization.write

import scala.collection.mutable
import scala.util.Try

/**
 *
 * 他のPOSTの情報に依存するPOST
 *
 * Date: 14/06/01.
 */
class DependentPost {
  implicit val formats = DefaultFormats
  type Req = Map[String, String]

  val FleetMax = 4

  // 現在進行中のStage情報がBattleResultで必要なので置いておく
  private[this] var mapNext: Option[data.MapStart] = None
  // 出撃中の艦隊番号
  private[this] var startFleet: Int = 0
  // 艦隊情報がRoute等で必要なので溜めておく
  private[this] var fleets: Seq[List[Int]] = Nil
  private[this] var firstFleet: List[Int] = Nil
  // KDock + CreateShipのデータが欲しいのでKDockIDをKeyにCreateShipを溜めておく
  private[this] val createShips: mutable.Map[Int, data.CreateShip] = mutable.Map()


  /** 第一艦隊の情報のみ変更。めんどいので特にサーバは更新しない */
  def henseiChange(req: Map[String, String]): Unit = {
    synchronized {
      val change = data.HenseiChange.fromMap(req)
      if(change.id == 1) {
        if(change.shipId == -1) firstFleet = firstFleet.filterNot(_ == firstFleet(change.shipIdx))
        else {
          firstFleet = Try {
            firstFleet.updated(change.shipIdx, change.shipId)
          }.getOrElse {
            firstFleet = firstFleet.filterNot(_ == change.shipId)
            firstFleet :+ change.shipId
          }
        }
      }
    }
  }

  def setCreateShip(req: Req): Unit = {
    synchronized {
      val createShip = data.CreateShip.fromMap(req)
      createShips(createShip.kDock) = createShip
    }
  }

  def createship(req: Req, obj: JValue)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    synchronized {
      val kdock = DeleteKDock.fromReq(req, obj)
      MFGHttp.post("/delete_kdock", write(kdock))
      createShips.remove(kdock.kDockId).foreach { cship =>
        val withId = CreateShipWithId(cship, kdock.shipId)
        MFGHttp.post("/createship", write(withId), 2)
        println(withId.summary)
      }
    }
  }

  def createitem(req: Req, obj: JValue)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    synchronized {
      firstFleet.lift(0).foreach { flag =>
        val createItem = data.CreateItem.from(req, obj, flag)
        MFGHttp.post("/createitem", write(createItem))
        println(createItem.summary)
      }
    }
  }

  def kdock(obj: JValue)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    synchronized {
      val docks = KDock.fromJson(obj).filterNot(_.completeTime == 0)
      MFGHttp.post("/kdock", write(docks))
      docks.foreach { dock =>
        createShips.get(dock.id).foreach { cShip =>
          val dat = CreateShipAndDock(cShip, dock)
          MFGHttp.post("/createship", write(dat))
          createShips.remove(dock.id)
        }
        println(dock.summary)
      }
    }
  }

  def deckport(obj: JValue)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    synchronized {
      val decks = DeckPort.fromJson(obj)
      firstFleet = extractFleetShips(decks)(1)
      fleets = (1 to FleetMax).map(extractFleetShips(decks))
      if(decks.nonEmpty) MFGHttp.post("/deckport", write(decks))
      decks.map(_.summary).foreach(println)
    }
  }

  private def extractFleetShips(decks: Iterable[DeckPort])(num: Int): List[Int] =
    decks.find(_.id == num).map(_.ships).getOrElse(Nil)

  def mapStart(req: Req, obj: JValue)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    synchronized {
      val next = data.MapStart.fromJson(obj)
      mapNext = Some(next)
      startFleet = req("api_deck_id").toInt
      println(next.summary)
    }
  }

  def mapRoute(obj: JValue)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    synchronized {
      val next = data.MapStart.fromJson(obj)
      mapNext.foreach { dep =>
        val route = MapRoute.fromMapNext(dep, next, fleets(startFleet - 1))
        println(route)
        MFGHttp.post("/map_route", write(route))
        println(route.summary)
      }
      mapNext = Some(next)
      println(next.summary)
    }
  }

  def battleResult(obj: JValue)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    synchronized {
      val result = data.BattleResult.fromJson(obj)
      MFGHttp.post("/battle_result", write((result, mapNext)))
      println(result.summary)
    }
  }
}
