package com.ponkotuy.run

import com.ponkotuy.intercept.KCIntercepter
import com.ponkotuy.proxy.FinagleProxy
import com.ponkotuy.config.ClientConfig

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
object Main extends App {
  try {
    new FinagleProxy(ClientConfig.kcUrl, ClientConfig.proxyPort, new KCIntercepter).start()
  } catch {
    case e: ExceptionInInitializerError =>
      e.printStackTrace()
      println("application.confが存在しないか設定が無効です。application.conf.sampleをコピーして設定しましょう")
  }
}
