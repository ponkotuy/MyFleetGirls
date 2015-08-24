package tool.swf

import java.io._

import com.jpexs.decompiler.flash.tags.{DefineSpriteTag, PlaceObject2Tag}
import models.db.CellPosition

import scala.collection.JavaConverters._
import scala.collection.breakOut

case class MapData(bytes: Array[Byte], cells: Seq[Cell])

object MapData {
  def fromFile(file: File): Option[MapData] = {
    val swf = WrappedSWF.fromFile(file)
    for {
      image <- getImage(swf)
      cells = getCells(swf)
    } yield MapData(image, cells)
  }

  private def getImage(swf: WrappedSWF): Option[Array[Byte]] = {
    val (_, jpeg) = swf.getJPEG3s.maxBy { case (_, jpg) =>
      jpg.getRect.getWidth
    }
    WrappedSWF.imageToBytes(jpeg)
  }

  private def getCells(swf: WrappedSWF): Seq[Cell] = {
    swf.getSprites.map { case (i, sprite) =>
      Cell.fromTag(sprite)
    }.flatMap(identity)(breakOut)
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
