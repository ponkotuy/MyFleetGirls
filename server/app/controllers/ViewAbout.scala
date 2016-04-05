package controllers

import javax.inject.Inject

import play.api.mvc.Controller
import views.About

import scala.concurrent.ExecutionContext

/**
 *
 * @author ponkotuy
 * Date: 14/10/11.
 \*/
class ViewAbout @Inject()(implicit val ec: ExecutionContext) extends Controller {
  def setup = Common.actionAsync { Redirect(About.Top) }
  def changeLog = Common.actionAsync { Redirect(About.ChangeLog) }
  def faq = Common.actionAsync { Redirect(About.Faq) }
  def setupDetail = Common.actionAsync { Redirect(About.SetupDetail) }
}
