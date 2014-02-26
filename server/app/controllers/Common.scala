package controllers

import play.api.mvc._
import org.json4s._
import org.json4s.native.{ JsonMethods => J }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import com.ponkotuy.data.Auth

/**
 *
 * @author ponkotuy
 * Date: 14/02/25
 */
object Common extends Controller {
  implicit val formats = DefaultFormats

  val Ponkotu = 110136878L
  def authentication(request: Request[AnyContent])(f: (models.Auth) => Unit): Future[SimpleResult] = {
    Future {
      val optoptResult = for {
        json <- reqHead(request)("auth")
        auth <- J.parse(json).extractOpt[Auth]
      } yield {
        models.Auth.find(auth.id) match {
          case Some(old) if auth.nickname == old.nickname => Some(old)
          case Some(_) => None
          case _ => Some(models.Auth.create(auth))
        }
      }
      optoptResult.getOrElse(None) match {
        case Some(auth) =>
          f(auth)
          Ok("Success")
        case _ => Unauthorized("Failed Authorization")
      }
    }
  }

  def checkPonkotu(request: Request[AnyContent])(f: => Unit): Future[SimpleResult] = {
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

  def withData[T](request: Request[AnyContent])(f: T => Unit)(implicit mf: Manifest[T]): Unit = {
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
}
