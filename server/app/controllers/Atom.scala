package controllers

import models.atom.{UserFeed, ActivitiesFeed}
import play.api.mvc._
import Common._
import models.db

/**
 *
 * @author ponkotuy
 * Date: 14/12/08.
 */
object Atom extends Controller {
  def activities() = actionAsync {
    val xs = Common.readActivities(0, 20, 0)
    val feed = ActivitiesFeed(xs).buildFeed()
    Ok(feed.toString).as("application/atom+xml")
  }

  def userActivities(memberId: Long) = actionAsync {
    val ndocks = db.NDock.findAllByUserWithName(memberId)
    val kdocks = db.KDock.findAllByUserWithName(memberId)
    val missions = db.Mission.findByUserWithFlagship(memberId)
    val acts = (kdocks ++ ndocks ++ missions).filter(_.completeTime <= System.currentTimeMillis()).sortBy(-_.completeTime)
    db.Admiral.find(memberId).map { admiral =>
      val feed = UserFeed(acts, admiral).buildFeed()
      Ok(feed.toString).as("application/atom+xml")
    }.getOrElse(NotFound(s"Not Found UserId = ${memberId}}"))
  }
}
