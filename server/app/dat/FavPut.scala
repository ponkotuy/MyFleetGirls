package dat

import play.api.data.Form
import play.api.data.Forms._

/**
 * Date: 14/11/07.
 */
case class FavPut(url: String, title: Option[String])

object FavPut {
  val form = Form(mapping("url" -> text, "title" -> optional(text))(FavPut.apply)(FavPut.unapply))
}
