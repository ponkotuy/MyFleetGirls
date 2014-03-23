package com.ponkotuy.tool

import java.io.{FileOutputStream, File}
import scala.util.Random

/**
 *
 * @author ponkotuy
 * Date: 14/03/23.
 */
object TempFileTool {
  private val Fname = "tempfiletool_myfleet"

  def save(dat: Array[Byte], extension: String): File = {
    val tempFile = File.createTempFile(Fname, "." + extension)
    val fos = new FileOutputStream(tempFile.getPath)
    fos.write(dat)
    fos.close()
    tempFile
  }

  def create(extension: String): File = {
    File.createTempFile(Fname, "." + extension)
  }
}
