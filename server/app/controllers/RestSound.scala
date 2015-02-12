package controllers

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import models.db

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
object RestSound extends Controller {
  import Common._
  def ship(shipId: Int, soundId: Int) = Action.async {
    Future {
      db.ShipSound.find(shipId, soundId) match {
        case Some(record) => Ok(record.sound).as("audio/mp3")
        case _ => NotFound(s"Not Found Image (shipId=$shipId, soundId=$soundId)")
      }
    }
  }

  def shipHead(shipId: Int, soundId: Int) = Action.async {
    Future {
      db.ShipSound.find(shipId, soundId) match {
        case Some(record) => Ok(record.sound).as("audio/mp3")
        case _ => NotFound(s"Not Found Image (shipId=$shipId, soundId=$soundId)")
      }
    }
  }

  def shipKeyHead(shipKey: String, soundId: Int) = Action.async {
    Future {
      db.ShipSound.findKey(shipKey, soundId) match {
        case Some(record) => Ok(record.sound).as("audio/mp3")
        case _ => NotFound(s"Not Found Image (shipKey=$shipKey, soundId=$soundId)")
      }
    }
  }

  def random() = actionAsync {
    Ok(db.ShipSound.findRandom().sound).as("audio/mp3")
  }
}
