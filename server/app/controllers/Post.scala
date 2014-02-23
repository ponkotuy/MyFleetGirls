package controllers

import org.json4s._
import org.json4s.native.{ JsonMethods => J }
import play.api.mvc._
import com.ponkotuy.data.{Basic, Auth, Material}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.control.ControlThrowable

/**
 *
 * @author ponkotuy
 * Date: 14/02/21.
 */
object Post extends Controller {
  implicit val formats = DefaultFormats

  private def authentication(request: Request[AnyContent])(f: (models.Auth) => Unit): Future[SimpleResult] = {
    val optFutureResult: Option[Future[models.Auth]] = for {
      json <- reqHead(request)("auth")
      auth <- J.parse(json).extractOpt[Auth]
    } yield {
      models.Auth.find(auth.id).flatMap {
        case Some(old) if auth.nickname == old.nickname => Future(old)
        case Some(_) => authFail
        case _ => models.Auth.create(auth)
      }
    }
    optFutureResult.getOrElse(authFail).map { auth =>
      f(auth)
      Ok("Success")
    }
  }

  class AuthenticationException extends ControlThrowable
  private def authFail[T]: Future[T] = Future.failed[T](new AuthenticationException)

  private def withData[T](request: Request[AnyContent])(f: T => Unit)(implicit mf: Manifest[T]): Unit = {
    for {
      json <- reqHead(request)("data")
      data <- J.parse(json).extractOpt[T]
    } { f(data) }
  }

  private def reqHead(request: Request[AnyContent])(key: String): Option[String] = {
    for {
      form <- request.body.asFormUrlEncoded
      results <- form.get(key)
      one <- results.headOption
    } yield one
  }

  def basic = Action.async { request =>
    authentication(request) { auth =>
      withData[Basic](request) { basic =>
        models.Basic.create(basic, auth.id)
      }
    }
  }

  def material = Action.async { request =>
    authentication(request) { auth =>
      withData[Material](request) { material =>
        models.Material.create(material, auth.id)
      }
    }
  }
}
