package com.ponkotuy.parser

import scala.concurrent.ExecutionContext.Implicits._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import dispatch._
import com.ponkotuy.util.Log
import com.ponkotuy.data
import com.ponkotuy.data.{master, CreateShipAndDock, Auth}
import com.ponkotuy.config.ClientConfig
import scala.collection.mutable

/**
 *
 * @author ponkotuy
 * Date: 14/03/03
 */
class PostResponse extends Log {
  import com.ponkotuy.parser.ResType._

  implicit val formats = Serialization.formats(NoTypeHints)

  val Ponkotu = 110136878L

  private[this] var auth: Option[Auth] = None
  // KDock + CreateShipのデータが欲しいのでKDockIDをKeyにCreateShipを溜めておく
  private[this] val createShips: mutable.Map[Int, data.CreateShip] = mutable.Map()
  // 旗艦データが必要（CreateItemとか）なので溜めておく
  private[this] var flagship: Option[Int] = None

  def parseAndPost(typ: ResType, req: Map[String, String], obj: JValue): Unit = {
    typ match {
      case Material =>
        val material = data.Material.fromJson(obj)
        post("/material", write(material))
      case Basic =>
        auth = Some(data.Auth.fromJSON(obj))
        val basic = data.Basic.fromJSON(obj)
        post("/basic", write(basic))
      case Ship3 =>
        val ship = data.Ship.fromJson(obj \ "api_ship_data")
        post("/ship", write(ship))
      case NDock =>
        val docks = data.NDock.fromJson(obj)
        post("/ndock", write(docks))
      case KDock =>
        val docks = data.KDock.fromJson(obj).filterNot(_.completeTime == 0)
        post("/kdock", write(docks))
        docks.foreach { dock =>
          createShips.get(dock.id).foreach { cShip =>
            val dat = CreateShipAndDock(cShip, dock)
            post("/createship", write(dat))
          }
        }
      case DeckPort =>
        val decks = data.DeckPort.fromJson(obj)
        flagship = decks.find(_.id == 1).flatMap(_.ships.headOption)
        if(decks.nonEmpty) post("/deckport", write(decks))
      case CreateShip =>
        val createShip = data.CreateShip.fromMap(req)
        createShips(createShip.kDock) = createShip
      case CreateItem =>
        flagship.foreach { flag =>
          val createItem = data.CreateItem.from(req, obj, flag)
          post("/createitem", write(createItem))
        }
      case LoginCheck | Ship2 | Deck | Practice | Record | GetShip | Charge | MissionStart => // No Need
      case HenseiChange | HenseiLock | GetOthersDeck => // No Need
      case MasterMapArea | MasterSType | MasterUseItem | MasterFurniture => // No Need
      case MasterShip =>
        if(checkPonkotu) {
          val ships = master.MasterShip.fromJson(obj)
          post("/master/ship", write(ships))
        }
      case MasterMission =>
        if(checkPonkotu) {
          val missions = master.MasterMission.fromJson(obj)
          post("/master/mission", write(missions))
        }
      case MasterSlotItem =>
        if(checkPonkotu) {
          val items = master.MasterSlotItem.fromJson(obj)
          post("/master/slotitem", write(items))
        }
      case _ =>
        info(s"ResType: $typ")
        info(s"Req: $req")
        jsonInfo(obj)
    }
  }

  private def post(uStr: String, data: String): Unit = {
    Http(url(ClientConfig.postUrl + uStr) << Map("auth" -> write(auth), "data" -> data)).either.foreach {
      case Left(e) => error("POST Error"); error(e)
      case Right(res) =>
        info(s"POST Success: ($uStr, $data)")
        if(res.getStatusCode >= 400)
          error(s"Error Response ${res.getStatusCode}\n${res.getStatusText}\n${res.getResponseBody("UTF-8")}")
    }
  }

  private def checkPonkotu: Boolean = auth.map(_.id) == Some(Ponkotu)
}
