package controllers

import dat.FavPut
import play.api.mvc.Controller
import Common._

import scala.util.Try

/**
 * Date: 14/11/07.
 */
object Fav extends Controller {
  def put() = formAsync { implicit request =>
    val favput = FavPut.form.bindFromRequest.get
    val memberIdOpt = request.session.get("memberId").map(_.toLong)
    memberIdOpt.filter(uuidCheck(_, request.session.get("key"))).map { memberId =>
      Try {
        models.Favorite.create(memberId, favput.url)
        Ok("Success")
      }.getOrElse(BadRequest("Duplicated etc..."))
    }.getOrElse(Unauthorized("Authentication failure"))
  }

  def getCount(url: String) = actionAsync {
    Ok(models.Favorite.count(url).toString)
  }

  def isFaved(url: String) = actionAsync { implicit request =>
    val memberIdOpt = request.session.get("memberId").map(_.toLong)
    val result: Boolean = memberIdOpt.exists { memberId =>
      models.Favorite.isFaved(memberId, url)
    }
    Ok(result.toString)
  }
}
