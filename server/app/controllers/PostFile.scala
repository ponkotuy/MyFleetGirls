package controllers

import play.api.mvc._
import Common._
import java.io.{ByteArrayOutputStream, InputStream, FileInputStream}
import tool.SWFTool
import models.db

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
object PostFile extends Controller {
  def ship(shipKey: String) = Action.async(parse.multipartFormData) { request =>
    val form = request.body.asFormUrlEncoded
    authentication(form) { auth =>
      request.body.file("image") match {
        case Some(ref) =>
          findKey(shipKey) { ship =>
            db.ShipImage.find(ship.id) match {
              case Some(si) =>
                if(si.filename.isDefined) Ok("Already Exists")
                else {
                  db.ShipImage(si.id, si.image, Some(shipKey), si.memberId).save() // filenameをupdate
                  Ok("Updated Ship Image Key")
                }
              case None =>
                val swfFile = ref.ref.file
                val imageFile = SWFTool.extractJPG(swfFile, 5)
                val image = readAll(new FileInputStream(imageFile))
                db.ShipImage.create(ship.id, image, shipKey, auth.id)
                Ok("Success")
            }
          }
        case None => BadRequest("Need Image")
      }
    }
  }

  def sound(shipKey: String, soundId: Int) = Action.async(parse.multipartFormData) { request =>
    val form = request.body.asFormUrlEncoded
    authentication(form) { auth =>
      request.body.file("sound") match {
        case Some(ref) =>
          findKey(shipKey) { ship =>
            val mp3File = ref.ref.file
            val sound = readAll(new FileInputStream(mp3File))
            try {
              db.ShipSound.create(ship.id, soundId, sound)
              Ok("Success")
            } catch {
              case e: Throwable => Ok("Already Exists")
            }
          }
        case _ => BadRequest("Need Image")
      }
    }
  }

  private def readAll(is: InputStream): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    val buf = new Array[Byte](1024)
    var len = is.read(buf)
    while(len >= 0) {
      baos.write(buf, 0, len)
      len = is.read(buf)
    }
    baos.toByteArray
  }

  private def findKey(key: String)(f: db.MasterShipBase => Result) = {
    db.MasterShipBase.findByFilename(key) match {
      case Some(ship) => f(ship)
      case None => BadRequest("Wrong Filename or Not Found Master Data")
    }
  }
}
