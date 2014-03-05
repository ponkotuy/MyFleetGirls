package com.ponkotuy.util

import com.twitter.logging.Logger
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

  protected def debug(obj: Any): Unit = logger.debug(obj.toString.take(1000))
  protected def info(obj: Any): Unit = logger.info(obj.toString.take(1000))
  protected def jsoninfo(json: JValue): Unit = logger.info {
    Try { pretty(render(json)).take(1000) }.getOrElse("JSONParseError")
  }
  protected def error(obj: Any): Unit = logger.error(obj.toString)
  protected def fatal(obj: Any): Unit = logger.fatal(obj.toString)
}
