package com.ponkotuy.parser

import scala.sys.process._
import java.io.{FileOutputStream, ByteArrayOutputStream, InputStream, File}

/**
 * Date: 14/03/21.
 */
object SWFTool {
  def extractJPG(dat: Array[Byte], no: Int)(f: File => Unit): Unit = {
    require(swfextractExists)
    val input = saveTempFile(dat, "swf")
    val output = createTempFile("jpg")
    Seq("swfextract", input, s"-j${no}", "-o", output.getPath).run()
    f(output)
  }

  def fwrapper(f: Array[Byte] => Unit)(is: InputStream): Unit = {
    val bytes = readAll(is)
    f(bytes)
  }

  def saveTempFile(dat: Array[Byte], extension: String): String = {
    val fname = "swfextract_sample_myfleet"
    val tempFile = File.createTempFile(fname, extension)
    val fos = new FileOutputStream(tempFile.getPath)
    fos.write(dat)
    fos.close()
    tempFile.getPath
  }

  def createTempFile(extension: String): File = {
    val fname = "swfextract_sample_myfleet"
    File.createTempFile(fname, extension)
  }

  def swfextractExists: Boolean = commandExists("swfextract", Seq("--help"))

  def commandExists(command: String, args: Seq[String]): Boolean =
    (command +: args).! != 127


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
