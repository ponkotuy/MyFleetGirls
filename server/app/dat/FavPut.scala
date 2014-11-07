package dat

import play.api.data.Form
import play.api.data.Forms._

/**
 * Date: 14/11/07.
 */
case class FavPut(url: String)

object FavPut {
  val form = Form(mapping("url" -> text)(FavPut.apply)(FavPut.unapply))
}
