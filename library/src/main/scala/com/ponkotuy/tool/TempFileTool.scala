package com.ponkotuy.tool

import java.io.{Closeable, File}
import java.nio.file.Files

/**
 *
 * @author ponkotuy
 * Date: 14/03/23.
 */
object TempFileTool {
  private val Fname = "tempfiletool_myfleet"

  def save[T](dat: Array[Byte], extension: String)(f: File => T): T = {
    val temp = Files.createTempFile(Fname, "." + extension)
    using(Files.newOutputStream(temp)) { buffer =>
      buffer.write(dat)
      buffer.flush()
      usingTmp(temp.toFile) { file =>
        f(file)
      }
    }
  }

  def create(extension: String)(f: File => Unit): Unit = {
    val temp = Files.createTempFile(Fname, "." + extension)
    using(Files.newOutputStream(temp)) { buffer =>
      usingTmp(temp.toFile) { file =>
        f(file)
      }
    }
  }

  def using[A <: Closeable, T](s: A)(f: A => T): T = {
    try f(s) finally s.close()
  }

  def usingTmp[T](file: File)(f: File => T): T = {
    try f(file) finally file.delete()
  }
}
