package ranking.common

import util.ehcache.TimeToLiveCache
import scala.concurrent.duration._

/**
 *
 * @author ponkotuy
 * Date: 15/01/29.
 */
class RankingElementCache(override val cacheName: String) extends TimeToLiveCache[Int, RankingElement] {
  override def liveSeconds = 60.minutes.toSeconds
  override def maxEntries = 10000
  override def default(k: Int): Option[RankingElement] = None
}
