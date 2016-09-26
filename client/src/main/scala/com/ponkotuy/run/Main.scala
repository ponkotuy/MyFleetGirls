package com.ponkotuy.run

import com.ponkotuy.build.BuildInfo
import com.ponkotuy.config.ClientConfig
import com.ponkotuy.http.MFGHttp
import com.ponkotuy.intercept.KCInterceptor
import com.ponkotuy.proxy.{KCFiltersSource, LittleProxy}
import com.ponkotuy.util.Log
import com.ponkotuy.value.KCServer
import io.netty.util.ResourceLeakDetector

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
object Main extends App with Log {
  logger.info("MyFleetGirls Proxy Starting...")
  ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED)
  try {
    logger.debug("information messeage printing.");
    message()

    val proxy = new LittleProxy(
      ClientConfig.proxyHost,
      ClientConfig.proxyPort,
      ClientConfig.upstreamProxyHost,
      new KCFiltersSource(KCServer.ips, new KCInterceptor())
    )
    logger.debug("proxy threads starting.")
    proxy.start()
  } catch {
    case e: ExceptionInInitializerError =>
      logger.info("Proxy Initializer Error", e)
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
      case e: Throwable => logger.error("Can't get information message.",e)
    }
    println()
  }
}
