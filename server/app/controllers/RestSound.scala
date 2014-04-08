package controllers

import scala.concurrent.ExecutionContext.Implicits._
import play.api.mvc._
import scala.concurrent.Future

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
object RestSound extends Controller {
  def ship(shipId: Int, soundId: Int) = Action.async {
    Future {
      models.ShipSound.find(shipId, soundId) match {
        case Some(record) => Ok(record.sound).as("audio/mp3")
        case _ => NotFound(s"Not Found Image (shipId=$shipId, soundId=$soundId)")
      }
    }
  }

  def shipHead(shipId: Int, soundId: Int) = Action.async {
    Future {
      models.ShipSound.find(shipId, soundId) match {
        case Some(record) => Ok(record.sound).as("audio/mp3")
        case _ => NotFound(s"Not Found Image (shipId=$shipId, soundId=$soundId)")
      }
    }
  }
}
