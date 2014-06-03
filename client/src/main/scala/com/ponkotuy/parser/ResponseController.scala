package com.ponkotuy.parser

import org.json4s._
import com.ponkotuy.util.Log
import com.ponkotuy.data

/**
 *
 * @author ponkotuy
 * Date: 14/03/03
 */
class ResponseController extends Log {
  import com.ponkotuy.parser.ResType._

  val lazyPost = new LazyAuthPost
  val dependent = new DependentPost

  def query(q: Query): Unit = {
    val typ = q.resType.get
    lazy val req = q.reqMap
    lazy val obj = q.resJson.get
    typ match {
      case ApiStart2 =>
        lazyPost { (a, b) => Post.master(obj)(a, b) }
      case Material =>
        lazyPost { (a, b) => Post.material(obj)(a, b) }
      case Basic =>
        basic(obj)
      case Ship3 =>
        lazyPost { (a, b) => Post.update_ship(obj)(a, b) }
      case NDock =>
        lazyPost { (a, b) => Post.ndock(obj)(a, b) }
      case KDock =>
        lazyPost { (a, b) => dependent.kdock(obj)(a, b) }
      case DeckPort =>
        lazyPost { (a, b) => dependent.deckport(obj)(a, b) }
      case Deck =>
        lazyPost { (a, b) => dependent.deckport(obj)(a, b) }
      case SlotItem =>
        lazyPost { (a, b) => Post.slotitem(obj)(a, b) }
      case PictureBook =>
        lazyPost { (a, b) => Post.book(obj)(a, b) }
      case MapInfo =>
        lazyPost { (a, b) => Post.mapinfo(obj)(a, b) }
      case CreateShip =>
        lazyPost { (a, b) => dependent.setCreateShip(req) }
      case GetShip =>
        lazyPost { (a, b) => dependent.createship(req, obj)(a, b) }
      case CreateItem =>
        lazyPost { (a, b) => dependent.createitem(req, obj)(a, b) }
      case HenseiChange =>
        lazyPost { (a, b) => dependent.henseiChange(req) }
      case SortieBattleResult =>
        lazyPost { (a, b) => dependent.battleResult(obj)(a, b) }
      case MapStart =>
        lazyPost { (a, b) => dependent.mapStart(obj)(a, b) }
      case MapNext =>
        lazyPost { (a, b) => dependent.mapRoute(obj)(a, b) }
      case Port =>
        basic(obj \ "api_basic")
        lazyPost { (a, b) => Post.ship(obj \ "api_ship")(a, b) }
        lazyPost { (a, b) => Post.material(obj \ "api_material")(a, b) }
        lazyPost { (a, b) => Post.ndock(obj \ "api_ndock")(a, b) }
        lazyPost { (a, b) => dependent.deckport(obj \ "api_deck_port")(a, b) }
      case QuestList =>
        lazyPost { (a, b) => Post.questlist(obj)(a, b) }
      case LoginCheck | Ship2 | Deck | UseItem | Practice | Record | MapCell | UpdateDeckName | Charge | MissionStart |
           KaisouPowerup | PracticeBattle | PracticeMidnightBattle | PracticeBattleResult |
           HenseiLock | GetOthersDeck | SortieBattle | ClearItemGet | NyukyoStart | MasterUseItem |
           MasterFurniture => // No Need
      case ShipSWF =>
        lazyPost { (a, b) => Post.swfShip(q)(a, b) }
      case SoundMP3 =>
        lazyPost { (a, b) => Post.mp3kc(q)(a, b) }
      case _ =>
        info(s"ResType: $typ")
        info(s"Req: ${q.reqCont}")
        q.resJson.map(jsonInfo(_))
    }
  }

  private def basic(obj: JValue): Unit = {
    val a = data.Auth.fromJSON(obj)
    lazyPost.setAuth(a)
    lazyPost { (a, b) => Post.basic(obj)(a, b) }
  }
}
