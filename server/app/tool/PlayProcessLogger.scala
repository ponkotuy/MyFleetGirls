package tool

import scala.sys.process.ProcessLogger
import play.Logger

/**
 *
 * @author ponkotuy
 * Date: 14/03/23.
 */
class PlayProcessLogger extends ProcessLogger {
  override def buffer[T](f: => T): T = f

  override def err(s: => String): Unit = Logger.info(s)

  override def out(s: => String): Unit = Logger.trace(s)
}
