package tool

import scala.sys.process._
import java.io.{FileOutputStream, ByteArrayOutputStream, InputStream, File}
import com.ponkotuy.tool.TempFileTool

/**
 *
 * @author ponkotuy
 * Date: 14/03/21.
 */
object SWFTool {
  def extractJPG(input: File, no: Int): File = {
    require(swfextractExists)
    val output = TempFileTool.create("jpg")
    Seq("swfextract", input.getPath, s"-j${no}", "-o", output.getPath) !! new PlayProcessLogger
    output
  }

  def fwrapper(f: Array[Byte] => Unit)(is: InputStream): Unit = {
    val bytes = readAll(is)
    f(bytes)
  }

  def swfextractExists: Boolean = commandExists("swfextract", Seq("--help"))

  def commandExists(command: String, args: Seq[String]): Boolean =
    (command +: args).!(new PlayProcessLogger) != 127

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
