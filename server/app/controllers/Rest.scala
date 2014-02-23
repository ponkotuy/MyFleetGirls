package controllers

import play.api.mvc.{Action, Controller}
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import scala.concurrent.ExecutionContext.Implicits._

/**
 *
 * @author ponkotuy
 * Date: 14/02/23
 */
object Rest extends Controller {
  implicit val formats = Serialization.formats(NoTypeHints)
  def materials(userId: Long) = Action.async { request =>
    models.Material.findAllByUser(userId).map { results =>
      Ok(write(results))
    }
  }
}
