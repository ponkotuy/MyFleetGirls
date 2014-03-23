package controllers

import play.api.mvc._
import org.json4s._
import org.json4s.native.{ JsonMethods => J }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import com.ponkotuy.data.Auth
import models.Admiral
import com.ponkotuy.value.Global

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
          case Some(_) => None: Option[Admiral]
          case _ => Some(models.Admiral.create(auth))
        }
      }
      optoptResult.getOrElse(None) match {
        case Some(admiral) => f(admiral)
        case _ => Unauthorized("Failed Authorization")
      }
    }
  }

  def checkPonkotu(request: Req)(f: => SimpleResult): Future[SimpleResult] = {
    Future {
      val optResult = for {
        json <- reqHead(request)("auth")
        auth <- J.parse(json).extractOpt[Auth]
        if Global.Admin.contains(auth.memberId)
      } yield true
      optResult match {
        case Some(true) =>
          f
          Ok("Success")
        case _ => Unauthorized("Failed Authorization")
      }
    }
  }

  def withData[T](request: Req)(f: T => SimpleResult)(implicit mf: Manifest[T]): SimpleResult = {
    val result = for {
      json <- reqHead(request)("data")
      data <- J.parse(json).extractOpt[T]
    } yield { f(data) }
    result.getOrElse(BadRequest("Request Error(JSON Parse Error? Header?)"))
  }

  def reqHead(request: Req)(key: String): Option[String] = {
    for {
      results <- request.get(key)
      one <- results.headOption
    } yield one
  }
}
