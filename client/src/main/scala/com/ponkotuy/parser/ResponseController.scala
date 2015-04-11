package com.ponkotuy.parser

/**
 *
 * @author ponkotuy
 * Date: 14/03/03
 */
/*
class ResponseController extends Log {
  import ResType._

  private[this] var initSended = false

  val lazyPost = new LazyAuthPost
  val dependent = new DependentPost

  def query(q: Query): Unit = {
    val typ = q.resType.get
    lazy val req = q.reqMap
    lazy val obj: JValue = {
      q.resJson match {
        case Left(value) => value
        case Right(message) =>
          println(message)
          JObject()
      }
    }
    typ match {
      case ApiStart2 =>
        lazyPost { (a, b) => Post.master(obj)(a, b) }
      case Material =>
        lazyPost { (a, b) => Post.material(obj)(a, b) }
      case Basic =>
        basic(obj, q.uri)
      case Ship2 =>
        lazyPost { (a, b) => Post.ship(obj)(a, b) }
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
      case SortieBattleResult | CombinedBattleResult =>
        lazyPost { (a, b) => dependent.battleResult(obj)(a, b) }
      case MapStart =>
        lazyPost { (a, b) => dependent.mapStart(req, obj)(a, b) }
      case MapNext =>
        lazyPost { (a, b) => dependent.mapRoute(obj)(a, b) }
      case Port =>
        basic(obj \ "api_basic", q.uri)
        lazyPost { (a, b) => Post.ship(obj \ "api_ship")(a, b) }
        lazyPost { (a, b) => Post.material(obj \ "api_material")(a, b) }
        lazyPost { (a, b) => Post.ndock(obj \ "api_ndock")(a, b) }
        lazyPost { (a, b) => dependent.deckport(obj \ "api_deck_port")(a, b) }
      case QuestList =>
        lazyPost { (a, b) => Post.questlist(obj)(a, b) }
      case RemodelSlotlist =>
        lazyPost { (a, b) => dependent.remodelSlotList(obj)(a, b) }
      case RemodelSlotlistDetail =>
        lazyPost { (a, b) => dependent.remodelDetail(obj, req)(a, b) }
      case RemodelSlot =>
        lazyPost { (a, b) => Post.remodelSlot(obj, req)(a, b) }
      case RankingList =>
        lazyPost { (a, b) => Post.rankingList(obj)(a, b) }
      case LoginCheck | Deck | UseItem | Practice | Record | MapCell | UpdateDeckName | Charge | MissionStart |
           KaisouPowerup | Remodeling | Marriage | KaisouLock | PracticeBattle | PracticeMidnightBattle |
           PracticeBattleResult | HenseiLock | GetOthersDeck | SortieBattle | ClearItemGet | NyukyoStart | MasterUseItem |
           MasterFurniture => // No Need
      case ShipSWF =>
        lazyPost { (a, b) => Post.swfShip(q)(a, b) }
      case SoundMP3 =>
        lazyPost { (a, b) => Post.mp3kc(q)(a, b) }
      case _ =>
        if(ClientConfig.Auth.master) {
          info(s"ResType: $typ")
          info(s"Req: ${q.reqCont}")
          q.resJson.left.map(jsonInfo(_))
        }
    }
  }

  private def basic(obj: JValue, uri: Uri): Unit = {
    val a = data.Auth.fromJSON(obj)
    lazyPost.setAuth(a)
    lazyPost { (a, b) => Post.basic(obj)(a, b) }
    if(!initSended) {
      for {
        host <- uri.host
        kcServer <- KCServer.fromIP(host)
      } {
        lazyPost { (a, b) => Post.admiralSettings(kcServer)(a, b)}
        initSended = true
      }
    }
  }
}
*/
