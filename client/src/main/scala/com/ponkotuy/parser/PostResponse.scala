package com.ponkotuy.parser

import scala.collection.mutable
import scala.util.Try
import java.io._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import com.ponkotuy.util.Log
import com.ponkotuy.data
import com.ponkotuy.data.{MapRoute, CreateShipWithId, master}
import org.jboss.netty.buffer.ChannelBuffer
import com.ponkotuy.tool.TempFileTool
import com.ponkotuy.config.ClientConfig

/**
 *
 * @author ponkotuy
 * Date: 14/03/03
 */
class PostResponse extends Log {
  import com.ponkotuy.parser.ResType._
  import com.ponkotuy.http._

  implicit val formats = Serialization.formats(NoTypeHints)

  // データ転送に毎回必要
  private[this] implicit var auth: Option[data.Auth] = None
  // KDock + CreateShipのデータが欲しいのでKDockIDをKeyにCreateShipを溜めておく
  private[this] val createShips: mutable.Map[Int, data.CreateShip] = mutable.Map()
  // 現在進行中のStage情報がBattleResultで必要なので置いておく
  private[this] var mapNext: Option[data.MapStart] = None
  // 艦隊情報がRoute等で必要なので溜めておく
  private[this] var firstFleet: List[Int] = Nil

  def post(q: Query): Unit = {
    val typ = q.resType.get
    lazy val req = q.reqMap
    lazy val obj = q.resJson.get
    typ match {
      case ApiStart2 =>
        if(ClientConfig.master) {
          val masterGraph = master.MasterShipGraph.fromJson(obj \ "api_mst_shipgraph")
          val filenames = masterGraph.map(it => it.id -> it.filename).toMap
          val masterShip = master.MasterShip.fromJson(obj \ "api_mst_ship", filenames)
          MFGHttp.masterPost("/master/ship", write(masterShip))
          val masterMission = master.MasterMission.fromJson(obj \ "api_mst_mission")
          MFGHttp.masterPost("/master/mission", write(masterMission))
          val masterSlotitem = master.MasterSlotItem.fromJson(obj \ "api_mst_slotitem")
          MFGHttp.masterPost("/master/slotitem", write(masterSlotitem))
          val masterSType = master.MasterSType.fromJson(obj \ "api_mst_stype")
          MFGHttp.masterPost("/master/stype", write(masterSType))
        }
      case Material =>
        material(obj)
      case Basic =>
        basic(obj)
      case Ship3 =>
        val update = data.Ship.fromJson(obj \ "api_ship_data")
        MFGHttp.post("/update_ship", write(update))
      case NDock =>
        ndock(obj)
      case KDock =>
        kdock(obj)
      case DeckPort =>
        deckport(obj)
      case Deck =>
        deckport(obj)
      case SlotItem =>
        val items = data.SlotItem.fromJson(obj)
        MFGHttp.post("/slotitem", write(items))
        println(s"所持装備数 -> ${items.size}")
      case PictureBook =>
        val books = data.Book.fromJson(obj)
        if(books.isEmpty) return
        books.head match {
          case _: data.ShipBook => MFGHttp.post("/book/ship", write(books))
          case _: data.ItemBook => MFGHttp.post("/book/item", write(books))
        }
      case MapInfo =>
        val maps = data.MapInfo.fromJson(obj)
        MFGHttp.post("/mapinfo", write(maps))
      case CreateShip =>
        val createShip = data.CreateShip.fromMap(req)
        createShips(createShip.kDock) = createShip
      case GetShip =>
        val id = (obj \ "api_ship_id").extract[Int]
        createShips.remove(req("api_kdock_id").toInt).foreach { cship =>
          val withId = CreateShipWithId(cship, id)
          MFGHttp.post("/createship", write(withId), 2)
          println(withId.summary)
        }
      case CreateItem =>
        firstFleet.lift(0).foreach { flag =>
          val createItem = data.CreateItem.from(req, obj, flag)
          MFGHttp.post("/createitem", write(createItem))
          println(createItem.summary)
        }
      case HenseiChange =>
        val change = data.HenseiChange.fromMap(req)
        if(change.id == 1) {
          if(change.shipId == -1) firstFleet = firstFleet.drop(change.shipIdx)
          else {
            firstFleet = Try {
              firstFleet.updated(change.shipIdx, change.shipId)
            }.getOrElse(firstFleet :+ change.shipId)
          }
        }
        // 第一艦隊の情報のみ変更。めんどいので特にサーバは更新しない
      case SortieBattleResult =>
        val result = data.BattleResult.fromJson(obj)
        MFGHttp.post("/battle_result", write((result, mapNext)))
        println(result.summary)
      case MapStart =>
        val next = data.MapStart.fromJson(obj)
        mapNext = Some(next)
        println(next.summary)
      case MapNext =>
        val next = data.MapStart.fromJson(obj)
        mapNext.foreach { dep =>
          val route = MapRoute.fromMapNext(dep, next, firstFleet)
          MFGHttp.post("/map_route", write(route))
          println(route.summary)
        }
        mapNext = Some(next)
        println(next.summary)
      case Port =>
        basic(obj \ "api_basic")
        ship(obj \ "api_ship")
        material(obj \ "api_material")
        ndock(obj \ "api_ndock")
        deckport(obj \ "api_deck_port")
      case LoginCheck | Ship2 | Deck | UseItem | Practice | Record | MapCell | Charge | MissionStart | KaisouPowerup |
          HenseiLock | GetOthersDeck | SortieBattle | ClearItemGet | NyukyoStart | MasterUseItem |
           MasterFurniture => // No Need
      case ShipSWF =>
        parseKey(q.uri).filterNot(MFGHttp.existsImage).foreach { key =>
          val swf = allRead(q.res.getContent)
          val file = TempFileTool.save(swf, "swf")
          MFGHttp.postFile("/swf/ship/" + key, "image")(file)
          println(s"初めての艦娘を見て画像を転送しました")
        }
      case SoundMP3 =>
        SoundUrlId.parseURL(q.uri).filterNot(MFGHttp.existsSound).foreach { case SoundUrlId(shipId, soundId) =>
          val sound = allRead(q.res.getContent)
          val file = TempFileTool.save(sound, "mp3")
          MFGHttp.postFile(s"/mp3/kc/${shipId}/${soundId}", "sound")(file)
          println(s"初めて (ShipID -> $shipId, SoundID -> $soundId) の声を聞いた")
        }
      case _ =>
        info(s"ResType: $typ")
        info(s"Req: ${q.reqCont}")
        q.resJson.map(jsonInfo(_))
    }
  }

  private def basic(obj: JValue): Unit = {
    auth = Some(data.Auth.fromJSON(obj))
    val basic = data.Basic.fromJSON(obj)
    MFGHttp.post("/basic", write(basic))
    println(basic.summary)
  }

  private def ndock(obj: JValue): Unit = {
    val docks = data.NDock.fromJson(obj)
    MFGHttp.post("/ndock", write(docks))
    docks.filterNot(_.shipId == 0).map(_.summary).foreach(println)
  }

  private def kdock(obj: JValue): Unit = {
    val docks = data.KDock.fromJson(obj).filterNot(_.completeTime == 0)
    MFGHttp.post("/kdock", write(docks))
    docks.foreach { dock =>
      createShips.get(dock.id).foreach { cShip =>
        val dat = data.CreateShipAndDock(cShip, dock)
        MFGHttp.post("/createship", write(dat))
        createShips.remove(dock.id)
      }
      println(dock.summary)
    }
  }

  private def deckport(obj: JValue): Unit = {
    val decks = data.DeckPort.fromJson(obj)
    firstFleet = decks.find(_.id == 1).map(_.ships).getOrElse(Nil)
    if(decks.nonEmpty) MFGHttp.post("/deckport", write(decks))
    decks.map(_.summary).foreach(println)
  }

  private def ship(obj: JValue): Unit = {
    val ship = data.Ship.fromJson(obj)
    MFGHttp.post("/ship", write(ship), ver = 2)
    println(s"所持艦娘数 -> ${ship.size}")
  }

  private def material(obj: JValue): Unit = {
    val material = data.Material.fromJson(obj)
    MFGHttp.post("/material", write(material))
    println(material.summary)
  }

  private def parseKey(str: String): Option[String] =
    Try {
      val filename = str.split('/').last
      filename.takeWhile(_ != '.')
    }.toOption

  private def allRead(cb: ChannelBuffer): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    cb.getBytes(0, baos, cb.readableBytes())
    baos.toByteArray
  }
}

case class SoundUrlId(shipId: Int, soundId: Int)

object SoundUrlId {
  val pattern = """.*/kcs/sound/kc(\d+)/(\d+).mp3""".r

  def parseURL(url: String): Option[SoundUrlId] = {
    url match {
      case pattern(ship, sound) => Try { SoundUrlId(ship.toInt, sound.toInt) }.toOption
      case _ => None
    }
  }
}
