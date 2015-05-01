package models.join

import honor.Honors
import models.db._

/**
 *
 * Date: 14/03/19.
 * @author ponkotuy
 */
case class User(
    admiral: Admiral,
    basic: Basic,
    map: String,
    setting: UserSettings,
    logined: Option[Long]) {
  def isMine: Boolean = logined.contains(admiral.id)
  def medals = basic.medals
  lazy val honor: Option[Honor] = Honors.fromUser(admiral.id, true).headOption
}
