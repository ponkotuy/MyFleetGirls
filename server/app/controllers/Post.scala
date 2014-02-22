package controllers

import org.json4s._
import org.json4s.native.{ JsonMethods => J }
import play.api.mvc.{AnyContent, Action, Controller, Request}
import com.ponkotuy.data.{Basic, Auth, Material}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

/**
 *
 * @author ponkotuy
 * Date: 14/02/21.
 */
object Post extends Controller {
  implicit val formats = DefaultFormats

  private def authentication(request: Request[AnyContent])(f: => Unit) = {
    val optFutureResult = for {
      json <- reqHead(request)("auth")
      auth <- J.parse(json).extractOpt[Auth]
    } yield {
      models.Auth.find(auth.id).map {
        case Some(old) => auth.nickname == old.nickname
        case _ => models.Auth.create(auth)
          true
      }
    }
    optFutureResult.getOrElse(Future(false)).map {
      case true =>
        f
        Ok("Success")
      case false => Unauthorized("Authentication Failure")
    }
  }

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
    authentication(request) {
      withData[Basic](request) { basic =>
        models.Basic.create(basic)
      }
    }
  }

  def material = Action.async { request =>
    authentication(request) {
      withData[Material](request) { material =>
        models.Material.create(material)
      }
    }
  }
}
