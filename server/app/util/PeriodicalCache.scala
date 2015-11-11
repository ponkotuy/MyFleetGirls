package util

import scala.concurrent.duration.Duration


/**
 * @author ponkotuy
 * Date: 15/07/28.
 *
 * 定期的に全体がRefreshされる
 */
class PeriodicalValue[A](interval: Duration, default: () => A) {
  private[this] var cache: A = default()
  private[this] var time = System.currentTimeMillis()
  val millis = interval.toMillis

  private def check(): Unit = synchronized {
    val now = System.currentTimeMillis()
    if(time + millis < now) {
      cache = default()
      time = now
    }
  }

  def apply(): A = {
    check()
    cache
  }
}

class PeriodicalCache[K, V](interval: Duration, default: () => Map[K, V]) extends PeriodicalValue[Map[K, V]](interval, default) {
  def get(key: K): Option[V] = {
    apply().get(key)
  }
}
