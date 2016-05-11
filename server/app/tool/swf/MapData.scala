package tool.swf

import java.io._
import javax.imageio.ImageIO

import com.jpexs.decompiler.flash.tags.{DefineBitsJPEG3Tag, DefineBitsLossless2Tag, DefineSpriteTag, PlaceObject2Tag}
import com.jpexs.decompiler.flash.types.RECT
import models.db.CellPosition

import scala.collection.JavaConverters._
import scala.collection.breakOut
import scala.util.Try

case class MapData(bytes: Array[Byte], cells: Seq[Cell])

object MapData {
  def fromFile(file: File): Option[MapData] = {
    val swf = WrappedSWF.fromFile(file)
    for {
      image <- getImage(swf)
      cells = getCells(swf)
      bytes <- getCourse(swf, image.getRect).flatMap { course =>
        // output png
        val fos = new FileOutputStream("result.png")
        fos.write(WrappedSWF.imageToBytes(course).get)
        fos.close()

        WrappedSWF.imageToIS(course).map { is =>
          composeImage(image.getImageData, is)
        }

      }.orElse(WrappedSWF.imageToBytes(image))
    } yield MapData(bytes, cells)
  }

  private def getImage(swf: WrappedSWF): Option[DefineBitsJPEG3Tag] = {
    Try {
      val (_, jpeg) = swf.getJPEG3s.maxBy { case (_, jpg) =>
        jpg.getRect.getWidth
      }
      jpeg
    }.toOption
  }

  /** いくらかのマップで航路を別画像にしているので、それらしきPNGを取得する */
  private def getCourse(swf: WrappedSWF, rect: RECT): Option[DefineBitsLossless2Tag] = {
    swf.getLossLessImages.values.find { case png =>
      png.getRect.getWidth == rect.getWidth && png.getRect.getHeight == rect.getHeight
    }
  }

  private def getCells(swf: WrappedSWF): Seq[Cell] = {
    swf.getSprites.map { case (i, sprite) =>
      Cell.fromTag(sprite)
    }.flatMap(identity)(breakOut)
  }

  private def composeImage(img1: InputStream, img2: InputStream): Array[Byte] = {
    val img1Io = ImageIO.read(img1)
    val img2Io = ImageIO.read(img2)
    val graphics = img1Io.createGraphics()
    graphics.drawImage(img2Io, 0, 0, null)
    graphics.dispose()
    val baos = new ByteArrayOutputStream()
    ImageIO.write(img1Io, "jpg", baos)
    baos.toByteArray
  }

  def main(args: Array[String]) = {
    args.foreach { arg =>
      val file = new File(arg)
      MapData.fromFile(file).foreach { map =>
        val fos = new FileOutputStream("result.jpg")
        try {
          fos.write(map.bytes)
        } finally {
          fos.close()
        }
      }
    }
  }
}

case class Cell(cell: Int, posX: Int, posY: Int) {
  def toCellPosition(areaId: Int, infoNo: Int) = CellPosition(areaId, infoNo, cell, posX, posY)
}

object Cell {
  val LineRegex = """line(\d+)""".r
  def fromTag(tag: DefineSpriteTag): Seq[Cell] = {
    val subtags = tag.getSubTags.asScala
    subtags.collect {
      case obj: PlaceObject2Tag =>
        for {
          name <- Option(obj.name)
          matcher <- LineRegex.findFirstMatchIn(name)
        } yield {
          Cell(matcher.group(1).toInt, obj.matrix.translateX / 20, obj.matrix.translateY / 20)
        }
    }.flatten
  }
}
