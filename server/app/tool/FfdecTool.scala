package tool

import java.io._

import com.jpexs.decompiler.flash.SWF
import com.jpexs.decompiler.flash.tags.{DefineBitsJPEG3Tag, DefineSpriteTag, PlaceObject2Tag}
import models.db.CellPosition

import scala.collection.JavaConverters._
import scala.collection.breakOut

case class MapData(bytes: Array[Byte], cells: Seq[Cell])

object MapData {
  def fromFile(file: File): Option[MapData] = {
    val is = new FileInputStream(file)
    val swf = new SWF(is, file.getAbsolutePath, "KanColleMap", false)
    for {
      image <- getImage(swf)
      cells = getCells(swf)
    } yield MapData(image, cells)
  }

  private def getImage(swf: SWF): Option[Array[Byte]] = {
    val ids = swf.getCharacters.asScala
    ids.collectFirst {
      case (i, jpeg: DefineBitsJPEG3Tag) =>
        val os = new ByteArrayOutputStream()
        output(jpeg.getImageData, os)
        os.toByteArray
    }
  }

  private def getCells(swf: SWF): Seq[Cell] = {
    val ids = swf.getCharacters.asScala
    ids.collect {
      case (i, sprite: DefineSpriteTag) =>
        Cell.fromTag(sprite)
    }.flatMap(identity)(breakOut)
  }

  val BufSize = 1024 * 4
  def output(is: InputStream, os: OutputStream): Unit = {
    val buf = new Array[Byte](BufSize)
    while(is.read(buf) >= 0) {
      os.write(buf)
    }
  }

}

case class Cell(cell: Int, posX: Int, posY: Int) {
  def toCellPosition(areaId: Int, infoNo: Int) = CellPosition(areaId, infoNo, cell, posX, posY)
}

object Cell {
  val LineRegex = """line(\d)""".r
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
