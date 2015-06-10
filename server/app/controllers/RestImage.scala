package controllers

import models.db
import play.api.mvc._
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
object RestImage extends Controller {
  import controllers.Common._

  def ship = shipCommon(_: Int, _: Int)

  def shipHead = shipCommon(_: Int, _: Int)

  // swfId 5 => 通常画像 7 => 中破画像 1 => 通常画像(small) 3 => 中破画像(small)
  private def shipCommon(shipId: Int, _swfId: Int) = actionAsync {
    val swfId = shipId match {
      case sid if sid > 900 => _swfId match {
        case 5 => 1
        case 7 => 3
        case _ => 1
      }
      case sid if sid > 500 => 1
      case _ => _swfId
    }
    db.ShipImage.find(shipId, swfId) match {
      case Some(record) => Ok(record.image).as("image/jpeg")
      case _ =>
        db.ShipImage.findAllBy(sqls"si.id = ${shipId}").headOption.map { head =>
          Ok(head.image).as("image/jpeg")
        }.getOrElse(NotFound(s"Not Found Image (id=$shipId, swfId=$swfId)"))
    }
  }

  def shipKeyHead(shipKey: String, version: Int) = actionAsync {
    val si = db.ShipImage.si
    db.ShipImage.findAllBy(sqls.eq(si.filename, shipKey).and.eq(si.version, version)).headOption.map { record =>
      Ok(record.image).as("image/jpeg")
    }.getOrElse(NotFound(s"Not Found Image (key=$shipKey)"))
  }
}
