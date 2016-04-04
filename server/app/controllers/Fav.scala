package controllers

import controllers.Common._
import models.db
import models.req.FavPut
import play.api.mvc.Controller
import scalikejdbc._

import scala.util.Try

/**
 * Date: 14/11/07.
 */
class Fav extends Controller {
  def put() = formAsync { implicit request =>
    val favput = FavPut.form.bindFromRequest.get
    val memberIdOpt = request.session.get("memberId").map(_.toLong)
    memberIdOpt.filter(uuidCheck(_, request.session.get("key"))).map { memberId =>
      Try {
        db.Favorite.create(memberId, favput.url, favput.title.getOrElse(""))
        favput.title.foreach { title => // 同じURLでTitleが違うのを見つけたらupdateする
          val olds = db.Favorite.findAllByUrl(favput.url, sqls"f.title <> ${title}")
          olds.foreach { _.copy(title = title).save() }
        }
        Ok("Success")
      }.getOrElse(BadRequest("Duplicated etc..."))
    }.getOrElse(unauthorized)
  }

  def getCount(url: String) = actionAsync {
    Ok(db.Favorite.count(url).toString)
  }

  def isFaved(url: String) = actionAsync { implicit request =>
    val memberIdOpt = request.session.get("memberId").map(_.toLong)
    val result: Boolean = memberIdOpt.exists { memberId =>
      db.Favorite.isFaved(memberId, url)
    }
    Ok(result.toString)
  }

  def delete(id: Long) = actionAsync { request =>
    val memberIdOpt = request.session.get("memberId").map(_.toLong)
    memberIdOpt.filter(uuidCheck(_, request.session.get("key"))).map { memberId =>
      db.Favorite.find(id).map { fav =>
        if(fav.memberId == memberId) {
          fav.destroy()
          Ok("Success")
        } else { unauthorized }
      }.getOrElse(NotFound("Not Found"))
    }.getOrElse(unauthorized)
  }

  private def unauthorized = Unauthorized("Authentication failure")
}
