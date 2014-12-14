package controllers

import play.api.mvc.Controller

/**
 *
 * @author ponkotuy
 * Date: 14/10/11.
 \*/
object ViewAbout extends Controller {
  def setup = Common.actionAsync { Ok(views.html.about.setup()) }
  def changeLog = Common.actionAsync { Ok(views.html.about.change_log()) }
  def faq = Common.actionAsync { Ok(views.html.about.faq()) }
  def setupDetail = Common.actionAsync { Ok(views.html.about.setup_detail())}
}
