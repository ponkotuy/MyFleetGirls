package util.abdera

import org.apache.abdera.model.Feed
import org.joda.time.DateTime
import util.Abdera

/**
 *
 * @author ponkotuy
 * Date: 14/12/08.
 */
trait Feedable {
  def id: String
  def title: String
  def subTitle: String = ""
  def updated: DateTime = DateTime.now()
  def authors: List[String] = Nil
  def links: List[String] = Nil
  def entries: List[Entriable]

  def buildFeed(): Feed = {
    val feed = Abdera.newFeed()
    feed.setId(id)
    feed.setTitle(title)
    if(subTitle.nonEmpty) feed.setSubtitle(subTitle)
    feed.setUpdated(updated.toDate)
    authors.foreach(feed.addAuthor)
    links.foreach(feed.addLink)
    entries.foreach { entry =>
      val raw = feed.addEntry()
      entry.buildEntry(raw)
    }
    feed
  }
}
