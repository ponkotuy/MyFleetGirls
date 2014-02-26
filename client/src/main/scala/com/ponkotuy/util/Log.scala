package com.ponkotuy.util

import com.twitter.logging.{LoggerFactory, Logger}
import org.json4s._
import org.json4s.native.JsonMethods._

/** Log Trait
  *
  * ログ使いたいところでwith Logするだけの簡単な
  *
  * @author ponkotuy
  * Date 14/02/22
  */
trait Log {
  private lazy val factory = new LoggerFactory(node = getClass.toString, level = Some(Logger.DEBUG))
  protected lazy val logger: Logger = factory()

  protected def debug(obj: Any): Unit = logger.debug(obj.toString)
  protected def info(obj: Any): Unit = logger.info(obj.toString)
  protected def jsoninfo(json: JValue): Unit =  logger.info(pretty(render(json)))
  protected def error(obj: Any): Unit = logger.error(obj.toString)
}
