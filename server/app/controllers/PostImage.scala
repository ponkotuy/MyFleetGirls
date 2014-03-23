package controllers

import play.api.mvc._
import Common._
import java.io.{ByteArrayOutputStream, InputStream, FileInputStream, File}

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
object PostImage extends Controller {
  def ship(shipId: Int) = Action.async(parse.multipartFormData) { request =>
    val form = request.body.asFormUrlEncoded
    authentication(form) { auth =>
      request.body.file("image").map { ref =>
        val file = ref.ref.file
        val image = readAll(new FileInputStream(file))
        models.ShipImage.create(shipId, image)
      } match {
        case Some(_) => Ok("Success")
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
