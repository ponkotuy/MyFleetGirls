package tool.swf

import java.awt.image.{BufferedImage, RenderedImage}
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

  def tagToImage(image: ImageTag): BufferedImage = image.getImage.getBufferedImage

  def imageToBytes(image: ImageTag): Option[Array[Byte]] = {
    getExt(image).map { ext =>
      val img = tagToImage(image)
      ImageIOWrapper.toBytes(img, ext)
    }
  }

  def imageToInputStream(image: ImageTag): Option[InputStream] = {
    getExt(image).map { ext =>
      val img = tagToImage(image)
      ImageIOWrapper.toInputStream(img, ext)
    }
  }

  def getExt(image: ImageTag): Option[String] = image match {
    case _: DefineBitsJPEG3Tag => Some("jpg")
    case _: DefineBitsTag => Some("jpg")
    case _: DefineBitsLossless2Tag => Some("png")
    case _ => None
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

object ImageIOWrapper {
  def toInputStream(image: RenderedImage, ext: String): InputStream = {
    val bytes = toBytes(image, ext)
    new ByteArrayInputStream(bytes)
  }

  def toBytes(image: RenderedImage, ext: String): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    println(ext, image.getWidth)
    ImageIO.write(image, ext, baos)
    baos.toByteArray
  }

  def deepCopy(image: BufferedImage): BufferedImage = {
    val cm = image.getColorModel
    val isAlphaPremultiplied = cm.isAlphaPremultiplied
    val raster = image.copyData(null)
    new BufferedImage(cm, raster, isAlphaPremultiplied, null)
  }
}
