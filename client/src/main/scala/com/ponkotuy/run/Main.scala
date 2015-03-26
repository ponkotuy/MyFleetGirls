package com.ponkotuy.run

import com.ponkotuy.build.BuildInfo
import com.ponkotuy.config.ClientConfig
import com.ponkotuy.http.MFGHttp
import com.ponkotuy.intercept.KCIntercepter
import com.ponkotuy.proxy.LittleProxy

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
object Main extends App {
  try {
    message()
    new LittleProxy(ClientConfig.proxyPort, new KCIntercepter).start()
  } catch {
    case e: ExceptionInInitializerError =>
      e.printStackTrace()
      println("application.confが存在しないか設定が無効です。application.conf.sampleをコピーして設定しましょう")
  }

  def message(): Unit = {
    println()
    println("---------------------------------------------")
    println(s"  Welcome to MyFleetGirls Client Ver ${BuildInfo.version}")
    println("---------------------------------------------")
    println()
    try {
      val url = s"${ClientConfig.post}/assets/message"
      MFGHttp.getOrig(url).foreach { str =>
        str.lines.foreach(println)
      }
    } catch {
      case e: Throwable =>
    }
    println()
  }
}
