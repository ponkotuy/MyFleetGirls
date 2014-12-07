package models.join

import models.db.Admiral
import org.json4s.JsonDSL._
import org.json4s._

/**
 * Date: 14/07/15.
 */
trait Activity {
  def title: String
  def message: String
  def admiral: Admiral
  def url: String
  def created: Long
  def id: String

  def nickname: String = admiral.nickname
  def memberId: Long = admiral.id

  def toJSON: JObject =
    ("title" -> title) ~
      ("message" -> message) ~
      ("nickname" -> admiral.nickname) ~
      ("memberId" -> admiral.id) ~
      ("url" -> url) ~
      ("created" -> created)
}
