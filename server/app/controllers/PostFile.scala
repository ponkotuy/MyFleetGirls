package controllers

import java.io.{ByteArrayOutputStream, FileInputStream, InputStream}

import controllers.Common._
import models.db
import play.api.mvc._
import scalikejdbc._
import tool.{SWFContents, SWFTool, SWFType}

import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/03/22.
 */
object PostFile extends Controller {
  def ship(shipKey: String, version: Int) = Action.async(parse.multipartFormData) { request =>
    val form = request.body.asFormUrlEncoded
    authentication(form) { auth =>
      request.body.file("image") match {
        case Some(ref) =>
          findKey(shipKey) { ship =>
            val si = db.ShipImage.si
            if(500 < ship.id && ship.id <= 900) { Ok("Unnecessary Enemy") }
            else if(db.ShipImage.countBy(sqls.eq(si.id, ship.id).and.eq(si.version, version)) > 0) Ok("Already Exists")
            else {
              val swfFile = ref.ref.file
              val contents = SWFTool.contents(swfFile)
              val isExec = contents.filter(_.typ == SWFType.Jpeg).flatMap { case SWFContents(id, _) =>
                Try {
                  SWFTool.extractJPG(swfFile, id) { file =>
                    val image = readAll(new FileInputStream(file))
                    db.ShipImage.create(ship.id, image, shipKey, auth.id, id, version)
                  }
                }.toOption
              }.nonEmpty
              if(isExec) Ok("Success") else BadRequest("Not Found Image")
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
