package controllers

import play.api.mvc._
import org.json4s._
import org.json4s.native.{ JsonMethods => J }
import org.json4s.native.Serialization.write
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data.Auth
import util.User

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
object Common extends Controller {
  type Req = Map[String, Seq[String]]
  implicit val formats = DefaultFormats

  def authAndParse[T](f: (models.Admiral, T) => SimpleResult)(implicit mf: Manifest[T]): Action[Req] = {
    Action.async(parse.urlFormEncoded) { request =>
      authentication(request.body) { auth =>
        withData[T](request.body) { data =>
          f(auth, data)
        }
      }
    }
  }

  /** 実際はCheckしてないです */
  def checkPonkotuAndParse[T](f: (T) => SimpleResult)(implicit mf: Manifest[T]): Action[Req] = {
    Action.async(parse.urlFormEncoded(1024*1024*2)) { request =>
      checkPonkotu(request.body) {
        withData[T](request.body) { data =>
          f(data)
        }
      }
    }
  }

  def authentication(request: Req)(f: (models.Admiral) => SimpleResult): Future[SimpleResult] = {
    Future {
      val optoptResult:Option[Option[models.Admiral]] = for {
        json <- reqHead(request)("auth")
        auth <- J.parse(json).extractOpt[Auth]
      } yield {
        models.Admiral.find(auth.memberId) match {
          case Some(old: models.Admiral) if old.authentication(auth) => Some(old)
          case Some(_) => None
          case _ => Some(models.Admiral.create(auth))
        }
      }
      optoptResult.getOrElse(None) match {
        case Some(admiral) => f(admiral)
        case _ => Unauthorized("Failed Authorization")
      }
    }
  }

  /** Checkしなくなりました */
  def checkPonkotu(request: Req)(f: => SimpleResult): Future[SimpleResult] = {
    Future {
      f
      Ok("Success")
    }
  }

  def withData[T](request: Req)(f: T => SimpleResult)(implicit mf: Manifest[T]): SimpleResult = {
    val result = for {
      json <- reqHead(request)("data")
      data <- J.parse(json).extractOpt[T]
    } yield { f(data) }
    result.getOrElse(BadRequest("Request Error(JSON Parse Error? Header?)"))
  }

  def userView(memberId: Long)(f: User => SimpleResult): Action[AnyContent] = Action.async {
    Future {
      getUser(memberId) match {
        case Some(user) => f(user)
        case _ => NotFound("ユーザが見つかりませんでした")
      }
    }
  }

  def reqHead(request: Req)(key: String): Option[String] = {
    for {
      results <- request.get(key)
      one <- results.headOption
    } yield one
  }

  private def getUser(memberId: Long): Option[User] = {
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
      User(auth, basic, nextMapView)
    }
  }

  def returnJson[A <: AnyRef](f: => A) = Action.async {
    Future {
      Ok(write(f)).as("application/json")
    }
  }

  def returnString[A](f: => A) = Action.async {
    Future {
      Ok(f.toString)
    }
  }
}
