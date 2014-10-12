package views

/**
 * JavaScriptライブラリのURLリスト
 *
 * @author ponkotuy
 * Date 14/10/13.
 */
object Js {
  val MomentLocales = "//cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.3/moment-with-locales.min.js"
  val Vue = "//cdnjs.cloudflare.com/ajax/libs/vue/0.10.6/vue.min.js"
  val Common = Coffee("common")
  def Coffee(fName: String) = controllers.routes.Assets.at(s"javascript/$fName.js")
}
