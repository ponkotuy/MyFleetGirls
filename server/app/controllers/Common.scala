package controllers

import play.api.mvc._
import org.json4s._
import org.json4s.native.{ JsonMethods => J }
import org.json4s.native.Serialization.write
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scalikejdbc._
import com.ponkotuy.data.{MyFleetAuth, Auth}
import dat.User
import tool.Authentication

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
object Common extends Controller {
  type Req = Map[String, Seq[String]]
  implicit val formats = DefaultFormats

  def authAndParse[T](f: (models.Admiral, T) => Result)(implicit mf: Manifest[T]): Action[Req] = {
    Action.async(parse.urlFormEncoded) { request =>
      authentication(request.body) { auth =>
        withData[T](request.body) { data =>
          f(auth, data)
        }
      }
    }
  }

  /** 実際はCheckしてないです */
  def checkPonkotuAndParse[T](f: (T) => Result)(implicit mf: Manifest[T]): Action[Req] = {
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
  def authentication(request: Req)(f: (models.Admiral) => Result): Future[Result] = {
    Future {
      reqHeadParse[Auth](request)("auth") match {
        case Some(oldAuth) =>
          Authentication.oldAuth(oldAuth) match {
            case Some(ad) =>
              reqHeadParse[MyFleetAuth](request)("auth2") match {
                case Some(auth) =>
                  if(auth.id == oldAuth.memberId && Authentication.myfleetAuthOrCreate(auth)) f(ad)
                  else Unauthorized("Failed Pasword Authentication")
                case None =>
                  if(models.MyFleetAuth.find(oldAuth.memberId).isEmpty) f(ad)
                  else Unauthorized("Require Password")
              }
            case None => Unauthorized("Failed Old Authentication")
          }
        case None => Unauthorized("Require Auth Data")
      }
    }
  }

  /** Checkしなくなりました */
  def checkPonkotu(request: Req)(f: => Result): Future[Result] = {
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

  def userView(memberId: Long)(f: User => Result): Action[AnyContent] = actionAsync { request =>
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
      auth <- models.Admiral.find(memberId)
      basic <- models.Basic.findByUser(memberId)
    } yield {
      val notClear = models.MapInfo.findAllBy(sqls"member_id = ${memberId} and cleared = false")
      val nextMapView = if(notClear.isEmpty) {
        if(models.MapInfo.find(54, memberId).isDefined) "全海域クリア" else "海域進捗未登録"
      } else {
        notClear.map(_.abbr).mkString("", ", ", "海域の攻略中")
      }
      val settings = models.UserSettings.find(memberId).getOrElse(models.UserSettings.empty(memberId))
      User(auth, basic, nextMapView, settings, logined)
    }
  }

  def uuidCheck(memberId: Long, key: Option[String]): Boolean = {
    val result = for {
      k <- key
      session <- models.Session.findByUser(memberId)
    } yield session.uuid.toString == k
    result.getOrElse(false)
  }

  def returnJson[A <: AnyRef](f: => A) = Action.async {
    Future {
      Ok(write(f)).as("application/json")
    }
  }

  def returnString[A](f: => A) = actionAsync { Ok(f.toString) }

  def actionAsync(f: => Result) = Action.async { request =>
    Future { f }
  }

  def actionAsync(f: (Request[AnyContent]) => Result) = Action.async { request =>
    Future { f(request) }
  }

  def formAsync(f: Request[Map[String, Seq[String]]] => Result) = Action.async(parse.urlFormEncoded) { request =>
    Future {
      f(request)
    }
  }
}
