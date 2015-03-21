package ranking

import models.db.MasterShipAfter
import util.ehcache.TimeToLiveCache

import scala.concurrent.duration._

/**
 * Thread Safe
 *
 * @author ponkotuy
 * Date: 15/01/10.
 */
object EvolutionBase {
  def apply(shipId: Int): Int = {
    Afters.get(shipId) match {
      case Some(afterId) => apply(afterId)
      case None => shipId
    }
  }

  // 大鯨→龍鳳、U-511→呂500など名称変更が入る艦娘のid mapping
  val Aliases = Map(431 -> 436, 184 -> 185, 35 -> 147)

  /* TODO: EHCacheの厳密なsizeを求めるのは非常に重く、EHCacheを使うべきでない */
  object Afters extends TimeToLiveCache[Int, Int] {
    var size = Int.MaxValue
    val SizeCheckLock: AnyRef = new AnyRef
    val SetAllLock: AnyRef = new AnyRef

    val cacheName = "evolutionBase"
    val liveSeconds = 60.minutes.toSeconds
    val maxEntries = 10000

    private def replace(): Unit = SetAllLock.synchronized {
      removeAll()
      val all = MasterShipAfter.findAll()
        .filterNot(_.aftershipid == 0)
        .map { ship => ship.aftershipid -> ship.id }.toMap
      putAll(all)
      size = getSize
    }

    override def get(sid: Int): Option[Int] = {
      SizeCheckLock.synchronized {
        if(getStrictSize < size) replace()
      }
      super.get(sid)
    }

    override protected def default(k: Int): Option[Int] = None
  }
}
