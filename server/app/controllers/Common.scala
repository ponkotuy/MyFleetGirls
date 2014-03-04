package controllers

import play.api.mvc._
import org.json4s._
import org.json4s.native.{ JsonMethods => J }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import com.ponkotuy.data.Auth
import models.Admiral

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
object Common extends Controller {
  implicit val formats = DefaultFormats

  val Ponkotu = 10007732L

  def authAndParse[T](f: (models.Admiral, T) => SimpleResult)(implicit mf: Manifest[T]): Action[AnyContent] = {
    Action.async { request =>
      authentication(request) { auth =>
        withData[T](request) { data =>
          f(auth, data)
        }
      }
    }
  }

  def checkPonkotuAndParse[T](f: (T) => SimpleResult)(implicit mf: Manifest[T]): Action[AnyContent] = {
    Action.async { request =>
      checkPonkotu(request) {
        withData[T](request) { data =>
          f(data)
        }
      }
    }
  }

  def authentication(request: Request[AnyContent])(f: (models.Admiral) => SimpleResult): Future[SimpleResult] = {
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

  def checkPonkotu(request: Request[AnyContent])(f: => SimpleResult): Future[SimpleResult] = {
    Future {
      val optResult = for {
        json <- reqHead(request)("auth")
        auth <- J.parse(json).extractOpt[Auth]
        if auth.id == Ponkotu
      } yield true
      optResult match {
        case Some(true) =>
          f
          Ok("Success")
        case _ => Unauthorized("Failed Authorization")
      }
    }
  }

  def withData[T](request: Request[AnyContent])(f: T => SimpleResult)(implicit mf: Manifest[T]): SimpleResult = {
    val result = for {
      json <- reqHead(request)("data")
      data <- J.parse(json).extractOpt[T]
    } yield { f(data) }
    result.getOrElse(BadRequest("Request Error(JSON Parse Error? Header?)"))
  }

  private def reqHead(request: Request[AnyContent])(key: String): Option[String] = {
    for {
      form <- request.body.asFormUrlEncoded
      results <- form.get(key)
      one <- results.headOption
    } yield one
  }
}
