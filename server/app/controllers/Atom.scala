package controllers

import models.atom.ActivitiesFeed
import play.api.mvc._
import Common._

/**
 *
 * @author ponkotuy
 * Date: 14/12/08.
 */
object Atom extends Controller {
  def activities() = actionAsync {
    val xs = Rest.readActivities(0, 20, 0)
    val feed = ActivitiesFeed(xs).buildFeed()
    Ok(feed.toString).as("application/atom+xml")
  }
}
