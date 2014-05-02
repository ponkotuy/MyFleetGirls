package controllers

import play.api.mvc._
import Common._
import java.io.{ByteArrayOutputStream, InputStream, FileInputStream}
import tool.SWFTool
import models.ShipImage

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
          models.MasterShipBase.findByFilename(shipKey) match {
            case Some(ship) =>
              models.ShipImage.find(ship.id) match {
                case Some(si) =>
                  if(si.filename.isDefined) Ok("Already Exists")
                  else {
                    ShipImage(si.id, si.image, Some(shipKey), si.memberId).save() // filenameをupdate
                    Ok("Updated Ship Image Key")
                  }
                case None =>
                  val swfFile = ref.ref.file
                  val imageFile = SWFTool.extractJPG(swfFile, 5)
                  val image = readAll(new FileInputStream(imageFile))
                  models.ShipImage.create(ship.id, image, shipKey, auth.id)
                  Ok("Success")
              }
            case None => BadRequest("Wrong Filename or Not Found Master Data")
          }
        case None => BadRequest("Need Image")
      }
    }
  }

  def sound(shipId: Int, soundId: Int) = Action.async(parse.multipartFormData) { request =>
    val form = request.body.asFormUrlEncoded
    authentication(form) { auth =>
      request.body.file("sound") match {
        case Some(ref) =>
          val mp3File = ref.ref.file
          val sound = readAll(new FileInputStream(mp3File))
          try {
            models.ShipSound.create(shipId, soundId, sound)
            Ok("Success")
          } catch {
            case e: Throwable => Ok("Already Exists")
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
}
