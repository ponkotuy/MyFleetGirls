package com.ponkotuy.util

import com.twitter.logging.Logger

/** Log Trait
  *
  * ログ使いたいところでwith Logするだけの簡単な
  *
  * @author ponkotuy
  * Date 14/02/22
  */
trait Log {
  protected lazy val logger: Logger = Logger(getClass)

  protected def info(obj: Any) = logger.info(obj.toString)
  protected def error(obj: Any) = logger.error(obj.toString)
}
