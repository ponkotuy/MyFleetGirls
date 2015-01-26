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

  def save(dat: Array[Byte], extension: String)(f: File => Unit): Unit = {
    val temp = Files.createTempFile(Fname, "." + extension)
    using(Files.newOutputStream(temp)) { buffer =>
      buffer.write(dat)
      buffer.flush()
      usingTmp(temp.toFile) { file =>
        f(file)
        file.delete()
      }
    }
  }

  def create(extension: String)(f: File => Unit): Unit = {
    val temp = Files.createTempFile(Fname, "." + extension)
    using(Files.newOutputStream(temp)) { buffer =>
      usingTmp(temp.toFile) { file =>
        f(file)
        file.delete()
      }
    }
  }

  def using[A <% Closeable](s: A)(f: A => Unit): Unit = {
    try f(s) finally s.close()
  }

  def usingTmp(file: File)(f: File => Unit): Unit = {
    try f(file) finally file.delete()
  }
}
