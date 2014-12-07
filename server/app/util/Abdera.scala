package util

import org.apache.abdera.model.Feed

/**
 *
 * @author ponkotuy
 * Date: 14/12/08.
 */
object Abdera {
  private[this] val instance = new org.apache.abdera.Abdera()
  def newFeed(): Feed = instance.newFeed()
}
