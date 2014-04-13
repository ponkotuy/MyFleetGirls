package com.ponkotuy.util

import com.twitter.logging.{LoggerFactory, Logger}
import org.json4s._
import org.json4s.native.JsonMethods._
import scala.util.Try

/** Log Trait
  *
  * ログ使いたいところでwith Logするだけの簡単な
  *
  * @author ponkotuy
  * Date 14/02/22
  */
trait Log {
  private lazy val factory = new LoggerFactory(node = getClass.toString, level = Some(Logger.DEBUG))
  lazy val logger: Logger = factory()

  protected def debug(obj: Any, size: Int = 1000): Unit = logger.debug(obj.toString.take(size))
  protected def info(obj: Any, size: Int = 1000): Unit = logger.info(obj.toString.take(size))
  protected def jsonInfo(json: JValue, size: Int = 1000): Unit = logger.info {
    prettyJson(json).map(_.take(size)).getOrElse("JSONParseError")
  }
  protected def jsonAllInfo(json: JValue): Unit = logger.info {
    prettyJson(json).getOrElse("JSONParseError")
  }
  protected def error(obj: Any): Unit = logger.error(obj.toString)
  protected def fatal(obj: Any): Unit = logger.fatal(obj.toString)

  private def prettyJson(json: JValue): Try[String] = Try { pretty(render(json)) }

}
