package util.ehcache

import net.sf.ehcache.config.CacheConfiguration
import net.sf.ehcache.{Element, Cache, CacheManager}

import scala.collection.JavaConverters._
import scala.util.Try

/**
 * Created by yosuke on 15/01/10.
 */
trait TimeToLiveCache[K, V] {
  def cacheName: String
  def liveSeconds: Long
  def maxEntries: Int

  final lazy val cache = {
    val manager = CacheManager.getInstance()
    if(manager.cacheExists(cacheName)) {
      manager.getCache(cacheName)
    } else {
      val conf = new CacheConfiguration(cacheName, maxEntries)
        .eternal(false)
        .timeToLiveSeconds(liveSeconds)
      val x = new Cache(conf)
      manager.addCache(x)
      x
    }
  }

  def removeAll(): Unit = cache.removeAll()

  def putAll(xs: Map[K, V]): TimeToLiveCache[K, V] = {
    val elements = xs.map { case (k, v) => new Element(k, v) }.asJavaCollection
    cache.putAll(elements)
    this
  }

  def put(k: K, v: V): TimeToLiveCache[K, V] = {
    cache.put(new Element(k, v))
    this
  }

  def get(k: K): Option[V] = {
    Try { cache.get(k).getObjectValue.asInstanceOf[V] }.toOption.orElse {
      val result = default(k)
      result.foreach { v => put(k, v) }
      result
    }
  }

  def getSize: Int = cache.getSize

  protected def default(k: K): Option[V]
}
