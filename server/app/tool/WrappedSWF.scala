package tool

import java.io._

import com.jpexs.decompiler.flash.SWF
import com.jpexs.decompiler.flash.tags.base.ImageTag
import com.jpexs.decompiler.flash.tags.{DefineBitsTag, DefineBitsJPEG3Tag, DefineSpriteTag}

import scala.collection.JavaConverters._
import scala.collection.breakOut

/**
 *
 * @author ponkotuy
 * Date: 14/03/21.
 */
case class WrappedSWF(swf: SWF) {
  def getAllTags = swf.getCharacters.asScala

  def getJPEG3s: Map[Int, DefineBitsJPEG3Tag] = getAllTags.collect {
    case (i, jpg: DefineBitsJPEG3Tag) => (i: Int, jpg)
  }(breakOut)

  def getImages: Map[Int, ImageTag] = getAllTags.collect {
    case (i, image: ImageTag) => (i: Int, image)
  }(breakOut)

  def getSprites: Map[Int, DefineSpriteTag] = getAllTags.collect {
    case (i, spr: DefineSpriteTag) => (i: Int, spr)
  }(breakOut)
}

object WrappedSWF {
  def fromFile(file: File): WrappedSWF = {
    val is = new FileInputStream(file)
    WrappedSWF(new SWF(is, file.getAbsolutePath, file.getName, false))
  }

  def imageToBytes(image: ImageTag): Option[Array[Byte]] = {
    image match {
      case bits: DefineBitsTag =>
        val errLen = if(ImageTag.hasErrorHeader(bits.jpegData)) 4 else 0
        val is = new ByteArrayInputStream(
          bits.jpegData.getArray,
          bits.jpegData.getPos + errLen,
          bits.jpegData.getLength - errLen)
        Some(readAll(is))
      case _ => Option(image.getImageData).map(readAll)
    }
  }

  def readAll(is: InputStream): Array[Byte] = {
    val bout = new ByteArrayOutputStream()
    val buffer = new Array[Byte](1024)
    var len = is.read(buffer)
    while(len >= 0) {
      bout.write(buffer, 0, len)
      len = is.read(buffer)
    }
    bout.toByteArray
  }
}
