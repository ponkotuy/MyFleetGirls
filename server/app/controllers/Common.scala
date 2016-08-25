package controllers

import com.ponkotuy.data.{Auth, MyFleetAuth}
import models.db
import models.join.{Activity, User}
import org.json4s._
import org.json4s.native.Serialization.write
import org.json4s.native.{JsonMethods => J}
import play.api.mvc._
import scalikejdbc._
import tool.Authentication

import scala.concurrent.{ExecutionContext, Future}

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
object Common extends Controller {
  type Req = Map[String, Seq[String]]
  implicit val formats = DefaultFormats

  def authAndParse[T](f: (db.Admiral, T) => Result)(implicit mf: Manifest[T], ec: ExecutionContext): Action[Req] = {
    Action.async(parse.urlFormEncoded) { request =>
      authentication(request.body) { auth =>
        withData[T](request.body) { data =>
          f(auth, data)
        }
      }
    }
  }

  def authPonkotu(f: (db.Admiral) => Result)(implicit ec: ExecutionContext) = Action { req =>
    if(uuidCheck(10007732L, req.session.get("key"))) {
      db.Admiral.find(10007732L).fold(NotFound("Not found ponkotuy"))(f(_))
    } else Unauthorized("You are not ponkotuy")
  }

  /** 実際はCheckしてないです */
  def checkPonkotuAndParse[T](f: (T) => Result)(implicit mf: Manifest[T], ec: ExecutionContext): Action[Req] = {
    Action.async(parse.urlFormEncoded(1024*1024*2)) { request =>
      checkPonkotu(request.body) {
        withData[T](request.body) { data =>
          f(data)
        }
      }
    }
  }

  /**
   * 1. 旧ログイン系は必ず必要（さもないとデータが不足する）
   * 2. 新ログイン系は任意だが、一度でも認証させたら通さないと駄目
   */
  def authentication(request: Req)(f: (db.Admiral) => Result)(implicit ec: ExecutionContext): Future[Result] = {
    Future {
      reqHeadParse[Auth](request)("auth") match {
        case Some(oldAuth) =>
          Authentication.oldAuth(oldAuth) match {
            case Some(ad) =>
              reqHeadParse[MyFleetAuth](request)("auth2") match {
                case Some(auth) =>
                  if(auth.id == oldAuth.memberId && Authentication.myfleetAuthOrCreate(auth)) f(ad)
                  else Unauthorized("Password Authentication Failed")
                case None =>
                  if(db.MyFleetAuth.find(oldAuth.memberId).isEmpty) f(ad)
                  else Unauthorized("Password Required")
              }
            case None => Unauthorized("Old Protocol Authentication Failed")
          }
        case None => Unauthorized("Authentication Data Required")
      }
    }
  }

  /** Checkしなくなりました */
  def checkPonkotu(request: Req)(f: => Result)(implicit ec: ExecutionContext): Future[Result] = {
    Future {
      f
      Ok("Success")
    }
  }

  def withData[T](request: Req)(f: T => Result)(implicit mf: Manifest[T]): Result = {
    val result = for {
      json <- reqHead(request)("data")
      data <- J.parse(json).extractOpt[T]
    } yield { f(data) }
    result.getOrElse(BadRequest("Request Error(JSON Parse Error? Header?)"))
  }

  def userView(memberId: Long)(f: User => Result)(implicit ec: ExecutionContext): Action[AnyContent] = actionAsync { request =>
    getUser(memberId, uuidCheck(memberId, request.session.get("key"))) match {
      case Some(user) => f(user)
      case _ => NotFound("ユーザが見つかりませんでした")
    }
  }

  def reqHead(request: Req)(key: String): Option[String] = {
    for {
      results <- request.get(key)
      one <- results.headOption
    } yield one
  }

  def reqHeadParse[T](request: Req)(key: String)(implicit m: Manifest[T]): Option[T] = {
    for {
      head <- reqHead(request)(key)
      result <- J.parse(head).extractOpt[T]
    } yield result
  }

  def getUser(memberId: Long, logined: Boolean): Option[User] = {
    for {
      auth <- db.Admiral.find(memberId)
      basic <- db.Basic.findByUser(memberId)
    } yield {
      val notClear = db.MapInfo.findAllBy(sqls"member_id = ${memberId} and cleared = false")
      val nextMapView = if(notClear.isEmpty) {
        if(db.MapInfo.find(54, memberId).isDefined) "全海域クリア" else "海域進捗未登録"
      } else {
        notClear.map(_.abbr).mkString("", ", ", "海域の攻略中")
      }
      val settings = db.UserSettings.find(memberId).getOrElse(db.UserSettings.empty(memberId))
      User(auth, basic, nextMapView, settings, if(logined) Some(memberId) else None)
    }
  }

  def uuidCheck(memberId: Long, key: Option[String]): Boolean = {
    val result = for {
      k <- key
      session <- db.Session.findByUser(memberId)
    } yield session.uuid.toString == k
    result.getOrElse(false)
  }

  def returnJson[A <: AnyRef](f: => A)(implicit ec: ExecutionContext): Action[AnyContent] = returnJsonReq(_ => f)

  def returnJsonReq[A <: AnyRef](f: Request[AnyContent] => A)(implicit ec: ExecutionContext): Action[AnyContent] = Action.async { req =>
    Future {
      try {
        Ok(write(f(req))).as("application/json")
      } catch {
        case e: IllegalArgumentException => BadRequest(e.getMessage)
      }
    }
  }

  def returnString[A](f: => A)(implicit ec: ExecutionContext) = actionAsync { Ok(f.toString) }

  def actionAsync(f: => Result)(implicit ec: ExecutionContext) = Action.async { request =>
    Future {
      try {
        f
      } catch {
        case e: IllegalArgumentException => BadRequest(e.getMessage)
      }
    }
  }

  def actionAsync(f: (Request[AnyContent]) => Result)(implicit ec: ExecutionContext) = Action.async { request =>
    Future {
      try {
        f(request)
      } catch {
        case e: IllegalArgumentException => BadRequest(e.getMessage)
      }
    }
  }

  def formAsync(f: Request[Map[String, Seq[String]]] => Result)(implicit ec: ExecutionContext) = Action.async(parse.urlFormEncoded) { request =>
    Future {
      try {
        f(request)
      } catch {
        case e: IllegalArgumentException => BadRequest(e.getMessage)
      }
    }
  }

  val NGStage = Set((1, 1), (2, 2), (2, 3))
  def readActivities(from: Long, limit: Int, offset: Int, memberId: Long = 0L): List[Activity] = {
    val mr = db.MapRoute.mr
    val started = db.MapRoute.findWithUserBy(sqls.gt(mr.created, from).and(condMember(mr.memberId, memberId)), limit*8, offset)
        .filter(_.start.exists(_.start))
        .filterNot { it => NGStage.contains((it.areaId, it.infoNo)) }
    val mso = db.MasterShipOther.mso
    val rares = db.MasterShipOther.findAllBy(sqls.ge(mso.backs, 6)).map(_.id).toSet
    val drops = readRareDrops(from, memberId, rares, limit, offset)
    val createItems = readRareCreateItems(from, memberId, limit, offset)
    val createShips = readRareCreateShip(from, memberId, rares, limit, offset)
    (started ++ drops ++ createItems ++ createShips).sortBy(-_.created).take(limit)
  }

  private def readRareDrops(from: Long, memberId: Long, rares: Set[Int], limit: Int, offset: Int) = {
    val br = db.BattleResult.br
    val where = sqls.gt(br.created, from).and.isNotNull(br.getShipId).and(condMember(br.memberId, memberId))
    db.BattleResult.findWithUserBy(where, limit*8, offset)
        .filter(_.getShipId.exists(rares.contains))
  }

  private def readRareCreateItems(from: Long, memberId: Long, limit: Int, offset: Int) = {
    val msi = db.MasterSlotItem.msi
    val rareItems = db.MasterSlotItem.findAllBy(sqls.ge(msi.rare, 2)).map(_.id).toSet
    val ci = db.CreateItem.ci
    db.CreateItem.findWithUserBy(sqls.gt(ci.created, from).and(condMember(ci.memberId, memberId)), limit, offset)
        .filter { it => rareItems.contains(it.itemId) }
  }

  private def readRareCreateShip(from: Long, memberId: Long, rares: Set[Int], limit: Int, offset: Int) = {
    val cs = db.CreateShip.cs
    db.CreateShip.findWithUserBy(sqls.gt(cs.created, from).and(condMember(cs.memberId, memberId)), limit, offset)
        .filter { it => rares.contains(it.shipId) }
  }

  private def condMember(column: SQLSyntax, memberId: Long): Option[SQLSyntax] =
    if(memberId == 0L) None else Some(sqls.eq(column, memberId))

}
