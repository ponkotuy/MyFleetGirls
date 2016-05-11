package tool.swf

import java.io._
import javax.imageio.ImageIO

import com.jpexs.decompiler.flash.SWF
import com.jpexs.decompiler.flash.tags.base.ImageTag
import com.jpexs.decompiler.flash.tags.{DefineBitsJPEG3Tag, DefineBitsLossless2Tag, DefineBitsTag, DefineSpriteTag}

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

  def getLossLessImages: Map[Int, DefineBitsLossless2Tag] = getAllTags.collect {
    case (i, png: DefineBitsLossless2Tag) => (i: Int, png)
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
      case bits: DefineBitsTag => imageToIS(bits).map(readAll)
      case bits: DefineBitsLossless2Tag =>
        val baos = new ByteArrayOutputStream()
        ImageIO.write(bits.getImage.getBufferedImage, "png", baos)
        Some(baos.toByteArray)
      case _ => Option(image.getImageData).map(readAll)
    }
  }

  def imageToIS(image: ImageTag): Option[InputStream] = {
    image match {
      case bits: DefineBitsLossless2Tag =>
        val baos = new ByteArrayOutputStream()
        ImageIO.write(bits.getImage.getBufferedImage, "png", baos)
        val bais = new ByteArrayInputStream(baos.toByteArray)
        Option(bais)
      case bits: DefineBitsTag =>
        val errLen = if(ImageTag.hasErrorHeader(bits.jpegData)) 4 else 0
        val bais = new ByteArrayInputStream(
          bits.jpegData.getArray,
          bits.jpegData.getPos + errLen,
          bits.jpegData.getLength - errLen)
        Some(bais)
      case _ => None
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
