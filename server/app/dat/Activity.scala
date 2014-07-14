package dat

import models.Admiral
import org.json4s._
import org.json4s.JsonDSL._

/**
 * Date: 14/07/15.
 */
trait Activity {
  def title: String
  def message: String
  def admiral: Admiral
  def url: String

  def nickname: String = admiral.nickname
  def memberId: Long = admiral.id

  def toJSON: JObject =
    ("title" -> title) ~ ("message" -> message) ~ ("nickname" -> admiral.nickname) ~ ("memberId" -> admiral.id)
}
