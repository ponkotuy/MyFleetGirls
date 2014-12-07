package util.abdera

import org.apache.abdera.model.{Entry, Text}
import org.joda.time.DateTime


/**
 *
 * @author ponkotuy
 * Date: 14/12/08.
 */
trait Entriable {
  def id: String
  def title: String
  def summary: Summary
  def updated: DateTime = DateTime.now()
  def published: DateTime = DateTime.now()
  def links: List[String] = Nil

  def buildEntry(entry: Entry): Entry = {
    entry.setId(id)
    entry.setTitle(title)
    summary.setSummary(entry)
    entry.setUpdated(updated.toDate)
    entry.setPublished(published.toDate)
    links.foreach(entry.addLink)
    entry
  }
}

sealed abstract class Summary {
  def setSummary(entry: Entry): Entry
}

object Summary {
  case class Html(str: String) extends Summary {
    override def setSummary(entry: Entry): Entry = {
      entry.setSummaryAsHtml(str)
      entry
    }
  }

  case class Xhtml(xhtml: xml.Elem) extends Summary {
    override def setSummary(entry: Entry): Entry = {
      entry.setSummaryAsXhtml(xhtml.toString())
      entry
    }
  }

  case class Element(text: Text) extends Summary {
    override def setSummary(entry: Entry): Entry = {
      entry.setSummaryElement(text)
      entry
    }
  }

  case class Str(str: String) extends Summary {
    override def setSummary(entry: Entry): Entry = {
      entry.setSummary(str)
      entry
    }
  }
}
