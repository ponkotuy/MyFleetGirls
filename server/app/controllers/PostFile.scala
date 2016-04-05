package controllers

import java.io.FileInputStream
import javax.inject.Inject

import com.ponkotuy.value.ShipIds
import controllers.Common._
import models.db
import models.db.{CellPosition, MapImage}
import play.api.mvc._
import scalikejdbc._
import tool.swf.{MapData, WrappedSWF}

import scala.concurrent.ExecutionContext
import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
class PostFile @Inject()(implicit val ec: ExecutionContext) extends Controller {
  def ship(shipKey: String, version: Int) = Action.async(parse.multipartFormData) { request =>
    val form = request.body.asFormUrlEncoded
    authentication(form) { auth =>
      request.body.file("image") match {
        case Some(ref) =>
          findKey(shipKey) { ship =>
            val si = db.ShipImage.si
            if(ShipIds.isEnemy(ship.id)) { Ok("Unnecessary Enemy") }
            else if(db.ShipImage.countBy(sqls.eq(si.id, ship.id).and.eq(si.version, version)) > 0) Ok("Already Exists")
            else {
              val swfFile = ref.ref.file
              val swf = WrappedSWF.fromFile(swfFile)
              val isExec = swf.getImages.flatMap { case (id, imgTag) =>
                Try {
                  val image = WrappedSWF.imageToBytes(imgTag).get
                  db.ShipImage.create(ship.id, image, shipKey, auth.id, id, version)
                }.toOption
              }.nonEmpty
              if(isExec) Ok("Success") else BadRequest("Not found image")
            }
          }
        case None => BadRequest("Need image")
      }
    }
  }

  def map(areaId: Int, infoNo: Int, version: Int) = Action.async(parse.multipartFormData) { request =>
    val form = request.body.asFormUrlEncoded
    authentication(form) { auth =>
      request.body.file("map") match {
        case Some(ref) =>
          if(db.MapImage.find(areaId, infoNo, version.toShort).isDefined) Ok("Already exists")
          else {
            val swfFile = ref.ref.file
            MapData.fromFile(swfFile) match {
              case Some(mapData) =>
                MapImage.create(areaId, infoNo, mapData.bytes, version.toShort)
                val cp = CellPosition.cp
                if(CellPosition.countBy(sqls.eq(cp.areaId, areaId).and.eq(cp.infoNo, infoNo)) == 0) {
                  mapData.cells.map { cell =>
                    CellPosition.create(areaId, infoNo, cell.cell, cell.posX, cell.posY)
                  }
                }
                Ok("Success")
              case None => BadRequest("SWF parse error")
            }
          }
        case None => BadRequest("Need swf file")
      }
    }
  }

  def sound(shipKey: String, soundId: Int, version: Int) = Action.async(parse.multipartFormData) { request =>
    val form = request.body.asFormUrlEncoded
    authentication(form) { auth =>
      request.body.file("sound") match {
        case Some(ref) =>
          findKey(shipKey) { ship =>
            val mp3File = ref.ref.file
            val sound = WrappedSWF.readAll(new FileInputStream(mp3File))
            try {
              db.ShipSound.create(ship.id, soundId, version, sound)
              Ok("Success")
            } catch {
              case e: Exception => Ok("Already exists")
            }
          }
        case _ => BadRequest("Need sound")
      }
    }
  }

  private def findKey(key: String)(f: db.MasterShipBase => Result) = {
    db.MasterShipBase.findByFilename(key) match {
      case Some(ship) => f(ship)
      case None => Ok("Is enemy, wrong filename or Not found master data")
    }
  }
}
