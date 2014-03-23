package controllers

import play.api.mvc.{Action, Controller}
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
 *
 * @author ponkotuy
 * Date: 14/02/23
 */
object Rest extends Controller {
  implicit val formats = Serialization.formats(NoTypeHints)
  def materials(userId: Long) = Action.async { request =>
    Future {
      val results = models.Material.findAllByUser(userId)
      Ok(write(results))
    }
  }

  def basics(userId: Long) = Action.async { request =>
    Future {
      val results = models.Basic.findAllByUser(userId)
      Ok(write(results))
    }
  }

  def docks(memberId: Long) = Action.async { request =>
    Future {
      val results = models.NDock.findAllByUser(memberId)
      Ok(write(results))
    }
  }
}
