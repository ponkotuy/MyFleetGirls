package com.ponkotuy.util

import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Try

/** Log Trait
  *
  * ログ使いたいところでwith Logするだけの簡単な
  *
  * @author ponkotuy
  * Date 14/02/22
  */
trait Log {

  lazy val logger: Logger = LoggerFactory.getLogger(getClass)

  protected def debug(obj: Any, size: Int = 1000): Unit = logger.debug(obj.toString.take(size))
  protected def info(obj: Any, size: Int = 1000): Unit = logger.info(obj.toString.take(size))
  protected def jsonInfo(json: JValue, size: Int = 1000): Unit = logger.info {
    prettyJson(json).map(_.take(size)).getOrElse("JSONParseError")
  }
  protected def jsonAllInfo(json: JValue): Unit = logger.info {
    prettyJson(json).getOrElse("JSONParseError")
  }
  protected def error(obj: Any): Unit = logger.error(obj.toString)

  private def prettyJson(json: JValue): Try[String] = Try { pretty(render(json)) }
}
