package controllers

import scala.concurrent.ExecutionContext.Implicits._
import play.api.mvc._
import scala.concurrent.Future
import models.db

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
object RestImage extends Controller {
  def ship(shipId: Int) = Action.async {
    Future {
      db.ShipImage.find(shipId) match {
        case Some(record) => Ok(record.image).as("image/jpeg")
        case _ => NotFound(s"Not Found Image (id=$shipId)")
      }
    }
  }

  def shipHead(shipId: Int) = Action.async {
    Future {
      db.ShipImage.find(shipId) match {
        case Some(record) => Ok(record.image).as("image/jpeg")
        case _ => NotFound(s"Not Found Image (id=$shipId)")
      }
    }
  }

  def shipKeyHead(shipKey: String) = Action.async {
    Future {
      db.ShipImage.findByFilename(shipKey) match {
        case Some(record) => Ok(record.image).as("image/jpeg")
        case _ => NotFound(s"Not Found Image (key=$shipKey)")
      }
    }
  }
}
