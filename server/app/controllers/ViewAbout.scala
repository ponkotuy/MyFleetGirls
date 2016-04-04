package controllers

import play.api.mvc.Controller
import views.About

/**
 *
 * @author ponkotuy
 * Date: 14/10/11.
 \*/
class ViewAbout extends Controller {
  def setup = Common.actionAsync { Redirect(About.Top) }
  def changeLog = Common.actionAsync { Redirect(About.ChangeLog) }
  def faq = Common.actionAsync { Redirect(About.Faq) }
  def setupDetail = Common.actionAsync { Redirect(About.SetupDetail) }
}
