package controllers

import play.api.mvc.{Action, Controller}
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scalikejdbc.SQLInterpolation._

/**
 *
 * @author ponkotuy
 * Date: 14/02/23
 */
object Rest extends Controller {
  implicit val formats = Serialization.formats(NoTypeHints)
  def materials(userId: Long) = returnJson(models.Material.findAllByUser(userId))

  def basics(userId: Long) = returnJson(models.Basic.findAllByUser(userId))

  def docks(memberId: Long) = returnJson(models.NDock.findAllByUser(memberId))

  def createShips(memberId: Long, limit: Int, offset: Int, large: Boolean) = returnJson {
    models.CreateShip.findAllByUserWithName(memberId, large, limit, offset)
  }

  def createShipCount(memberId: Long, large: Boolean) =
    returnString(models.CreateShip.countByUser(memberId, large))

  def createItems(memberId: Long, limit: Int, offset: Int) = returnJson {
    models.CreateItem.findAllByUserWithName(memberId, limit, offset)
  }

  def createItemCount(memberId: Long) = returnString(models.CreateItem.countBy(sqls"member_id = ${memberId}"))

  private def returnJson[A <: AnyRef](f: => A) = Action.async {
    Future {
      Ok(write(f)).as("application/json")
    }
  }

  private def returnString[A](f: => A) = Action.async {
    Future {
      Ok(f.toString)
    }
  }
}
