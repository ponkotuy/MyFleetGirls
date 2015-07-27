package util

import scala.concurrent.duration.Duration

/**
 * @author ponkotuy
 * Date: 15/07/28.
 *
 * 定期的に全体がRefreshされるCache
 */
class PeriodicalCache[K, V](interval: Duration, default: () => Map[K, V]) {
  private[this] var cache: Map[K, V] = default()
  private[this] var time = System.currentTimeMillis()
  val millis = interval.toMillis

  private def check(): Unit = synchronized {
    val now = System.currentTimeMillis()
    if(time + millis < now) {
      cache = default()
      time = now
    }
  }

  def get(key: K): Option[V] = {
    check()
    cache.get(key)
  }
}
