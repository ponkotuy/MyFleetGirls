package controllers

import models.db
import play.api.mvc._
import scalikejdbc._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
object RestImage extends Controller {
  def ship = shipCommon(_: Int, _: Int)

  def shipHead = shipCommon(_: Int, _: Int)

  private def shipCommon(shipId: Int, swfId: Int) = Action.async {
    Future {
      db.ShipImage.find(shipId, swfId) match {
        case Some(record) => Ok(record.image).as("image/jpeg")
        case _ =>
          db.ShipImage.findAllBy(sqls"si.id = ${shipId}").headOption.map { head =>
            Ok(head.image).as("image/jpeg")
          }.getOrElse(NotFound(s"Not Found Image (id=$shipId, swfId=$swfId)"))
      }
    }
  }

  def shipKeyHead(shipKey: String) = Action.async {
    Future {
      db.ShipImage.findAllBy(sqls"si.filename = ${shipKey}").headOption.map { record =>
        Ok(record.image).as("image/jpeg")
      }.getOrElse(NotFound(s"Not Found Image (key=$shipKey)"))
    }
  }
}
