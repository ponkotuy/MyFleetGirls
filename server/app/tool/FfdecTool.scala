package tool

import java.io._

import com.jpexs.decompiler.flash.SWF
import com.jpexs.decompiler.flash.tags.{ShowFrameTag, PlaceObject2Tag, DefineSpriteTag, DefineBitsJPEG3Tag}
import models.db.CellPosition

import scala.collection.JavaConverters._

case class MapData(bytes: Array[Byte], cells: Seq[Cell])

object MapData {
  def fromFile(file: File): Option[MapData] = {
    val is = new FileInputStream(file)
    val swf = new SWF(is, file.getAbsolutePath, "KanColleMap", false)
    for {
      image <- getImage(swf)
      cells <- getCells(swf)
    } yield MapData(image, cells)
  }

  private def getImage(swf: SWF): Option[Array[Byte]] = {
    val ids = swf.getCharacters.asScala
    ids.collectFirst {
      case (i, jpeg: DefineBitsJPEG3Tag) =>
        jpeg.getData
    }
  }

  private def getCells(swf: SWF): Option[Seq[Cell]] = {
    val ids = swf.getCharacters.asScala
    ids.collectFirst {
      case (i, sprite: DefineSpriteTag) if withCell(sprite) =>
        Cell.fromTag(sprite)
    }
  }

  private def withCell(tag: DefineSpriteTag): Boolean = {
    val subtags = tag.getSubTags.asScala
    subtags.collectFirst {
      case frame: ShowFrameTag => true
    }.isDefined
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
