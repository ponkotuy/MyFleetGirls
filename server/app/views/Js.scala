package views

/**
 * JavaScriptライブラリのURLリスト
 *
 * @author ponkotuy
 * Date 14/10/13.
 */
object Js {
  val MomentLocales = "//cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.3/moment-with-locales.min.js"
  val Vue = "//cdnjs.cloudflare.com/ajax/libs/vue/0.11.0/vue.min.js"
  val Lodash = "//cdnjs.cloudflare.com/ajax/libs/lodash.js/2.4.1/lodash.min.js"
  val D3 = "//cdnjs.cloudflare.com/ajax/libs/d3/3.4.12/d3.min.js"
  val BootstrapSwitch = "//cdnjs.cloudflare.com/ajax/libs/bootstrap-switch/3.2.1/js/bootstrap-switch.min.js"
  val Common = Coffee("common")
  val JQueryUI = Lib("jquery-ui.min")

  def ScriptFlot(adds: String*) = {
    <script src="//cdnjs.cloudflare.com/ajax/libs/flot/0.8.2/jquery.flot.min.js"></script> ++
    adds.map { add =>
      <script src={s"//cdnjs.cloudflare.com/ajax/libs/flot/0.8.2/jquery.flot.$add.min.js"}></script>
    }
  }

  def ImportJqplot(plugins: String*) = {
    <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jqPlot/1.0.8/jquery.jqplot.min.css" />
    <script src="//cdnjs.cloudflare.com/ajax/libs/jqPlot/1.0.8/jquery.jqplot.min.js"></script> ++
      plugins.map { plugin =>
        <script src={s"//cdnjs.cloudflare.com/ajax/libs/jqPlot/1.0.8/plugins/jqplot.$plugin.min.js"}></script>
      }
  }

  // TODO tablesorterがuser/shipでのみ使われている
  // TODO d3がcshipでのみ使われている

  def Assets(path: String) = controllers.routes.Assets.at(path)
  def Coffee(fName: String) = Assets(s"javascript/$fName.js")
  def Lib(fName: String) = Assets(s"javascript/lib/$fName.js")
}

object Css {
  val BootstrapSwitch = "//cdnjs.cloudflare.com/ajax/libs/bootstrap-switch/3.2.1/css/bootstrap3/bootstrap-switch.min.css"
  val JQueryUI = {
      <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.css" />
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.2/jquery-ui.theme.min.css" />
        <link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.2/jquery-ui.structure.min.css" />
  }
}

object Image {
  def setup(name: String) = controllers.routes.Assets.at(s"image/setup/${name}.png")
}
