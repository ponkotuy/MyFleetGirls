package models.atom

import controllers.routes
import models.db.Admiral
import models.join.UserActivity
import org.joda.time.DateTime
import tool.Settings
import util.abdera.{Summary, Entriable, Feedable}

import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/12/15.
 */
case class UserFeed(xs: List[UserActivity], admiral: Admiral) extends Feedable {
  override def id: String = s"${Settings.atomCommonKey}:${url}"
  override def title: String = s"${admiral.nickname}提督のActivities"
  override def entries: List[Entriable] = xs.map(UserEntry)
  override def updated: DateTime = new DateTime(newest.map(_.completeTime).getOrElse(System.currentTimeMillis()))
  override def links: List[String] = List(url)

  val url: String = routes.UserView.dock(admiral.id).url
  def newest: Option[UserActivity] = Try { xs.maxBy(_.completeTime) }.toOption
}

case class UserEntry(x: UserActivity) extends Entriable {
  override def id: String = s"${Settings.atomCommonKey}${x.url}:${x.key}"
  override def summary: Summary = Summary.Str(x.message)
  override def title: String = x.title
  override def updated: DateTime = new DateTime(x.completeTime)
  override def published: DateTime = new DateTime(x.created)
  override def links: List[String] = List(x.url)
}
