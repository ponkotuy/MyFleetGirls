package controllers

import play.api.mvc._
import Common._
import java.io.{ByteArrayOutputStream, InputStream, FileInputStream}
import tool.SWFTool

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
object PostFile extends Controller {
  def ship(shipId: Int) = Action.async(parse.multipartFormData) { request =>
    val form = request.body.asFormUrlEncoded
    authentication(form) { auth =>
      request.body.file("image").map { ref =>
        val swfFile = ref.ref.file
        val imageFile = SWFTool.extractJPG(swfFile, 5)
        val image = readAll(new FileInputStream(imageFile))
        try {
          models.ShipImage.create(shipId, image)
        } catch {
          case e: Throwable => Ok("Already Exists")
        }
      } match {
        case Some(_) => Ok("Success")
        case _ => BadRequest("Need Image")
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
