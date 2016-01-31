package models.atom

import controllers.routes
import models.join.Activity
import org.joda.time.DateTime
import tool.Settings
import util.abdera.{Entriable, Feedable, Summary}

/**
 *
 * @author ponkotuy
 * Date: 14/12/08.
 */
case class ActivitiesFeed(xs: List[Activity]) extends Feedable {
  import ActivitiesFeed._
  override def id: String = s"${Settings.atomCommonKey}:${url}"
  override def title: String = "Activities -統計-"
  override def entries: List[Entriable] = xs.map(ActivityEntry)
  override def updated: DateTime = new DateTime(newest.created)
  override def links: List[String] = List(url)

  def newest: Activity = xs.maxBy(_.created)
}

object ActivitiesFeed {
  def url: String = routes.ViewSta.activities().toString
}

case class ActivityEntry(x: Activity) extends Entriable {
  override def id: String = s"${Settings.atomCommonKey}${x.url}:${x.id}"
  override def summary: Summary = Summary.Str(x.message)
  override def title: String = s"${x.nickname}提督の${x.title}"
  override def updated: DateTime = new DateTime(x.created)
  override def published: DateTime = new DateTime(x.created)
  override def links: List[String] = List(x.url)
}
