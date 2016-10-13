package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import tool.Settings

class Analytics @Inject()() extends Controller {
  def getId = Action {
    Settings.googleAnalyticsId.fold(NotImplemented("GoogleAnalyticsのIDが設定ファイルに設定されていない"))(Ok(_))
  }
}
