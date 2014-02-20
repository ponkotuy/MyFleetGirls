package controllers

import org.json4s._
import org.json4s.native.JsonMethods._
import play.api.mvc.{Action, Controller}
import com.ponkotuy.data.{Basic, Auth, Material}
import scala.concurrent.Future
import play.api.mvc.Request

/**
 *
 * @author ponkotuy
 * Date: 14/02/21.
 */
object Post extends Controller {
  implicit val formats = DefaultFormats

  private def authentication(f: => Unit)(implicit request: Request) = {
    val optFutureResult = for {
      json <- request.getQueryString("auth")
      auth <- parse(json).extractOpt[Auth]
    } yield {
      Auth.find(auth.id).map {
        case Some(old) => auth.startTime == old.startTime
        case _ => auth.save()
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

  private def withData[T](f: T => Unit)(implicit request: Request): Unit = {
    for {
      json <- request.getQueryString("data")
      data <- parse(json).extractOpt[T]
    } { f(data) }
  }

  def basic = Action.async { implicit request =>
    authentication {
      withData[Basic] { basic =>
        basic.save
      }
    }
  }

  def material = Action.async { implicit request =>
    authentication {
      withData[Material] { material =>
        material.save
      }
    }
  }
}
