package tool

import java.io.{ByteArrayOutputStream, File, InputStream}

import com.ponkotuy.tool.TempFileTool

import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/03/21.
 */
object SWFTool {
  import scala.sys.process._

  def extractJPG(input: File, no: Int)(f: File => Unit): Unit = {
    require(swfextractExists)
    TempFileTool.create("jpg") { output =>
      Seq("swfextract", input.getPath, s"-j${no}", "-o", output.getPath) !! new PlayProcessLogger
      f(output)
    }
  }

  def contents(input: File): Vector[SWFContents] = {
    val result = Seq("swfextract", input.getPath) !! new PlayProcessLogger
    SWFContents.parse(result)
  }

  def fwrapper(f: Array[Byte] => Unit)(is: InputStream): Unit = {
    val bytes = readAll(is)
    f(bytes)
  }

  private def swfextractExists: Boolean = commandExists("swfextract", Seq("--help"))

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

sealed abstract class SWFType {
  def arg: Char
}

object SWFType {
  case object Frame extends SWFType {
    override def arg: Char = 'f'
  }
  case object Font extends SWFType {
    override def arg: Char = 'F'
  }
  case object Jpeg extends SWFType {
    override def arg: Char = 'j'
  }
  case object Png extends SWFType {
    override def arg: Char = 'p'
  }
  case object Mp3 extends SWFType {
    override def arg: Char = 'm'
  }
  case object EmbeddedMp3 extends SWFType {
    override def arg: Char = 'M'
  }
  case object Sound extends SWFType {
    override def arg: Char = 's'
  }
  case object Shape extends SWFType {
    override def arg: Char = 'i'
  }

  val values = Array(Frame, Font, Jpeg, Png, Mp3, EmbeddedMp3, Sound, Shape)
  def fromChar(c: Char): Option[SWFType] = values.find(_.arg == c)
}

case class SWFContents(id: Int, typ: SWFType)

object SWFContents {
  val ParseArg = """.*\[-(.)\].*""".r
  val ParseIds = """.*ID\(s\)(.+)$""".r
  def parse(str: String): Vector[SWFContents] = {
    str.lines.flatMap { line =>
      val typ = line match {
        case ParseArg(arg) => arg.headOption.flatMap(SWFType.fromChar)
        case _ => None
      }
      val ids: Array[String] = line match {
        case ParseIds(x) => x.filterNot(_ == ' ').split(',')
        case _ => Array()
      }
      for {
        idStr <- ids
        id <- Try { idStr.toInt }.toOption
        t <- typ
      } yield SWFContents(id, t)
    }
  }.toVector
}
