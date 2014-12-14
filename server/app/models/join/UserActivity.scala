package models.join

import org.json4s.JsonDSL._
import org.json4s._

/**
 * Date: 14/07/15.
 */
trait UserActivity {
  def title: String
  def message: String
  def url: String
  def completeTime: Long
  def key: String

  def toJSON: JObject =
    ("title" -> title) ~
      ("message" -> message) ~
      ("url" -> url) ~
      ("created" -> completeTime)
}
