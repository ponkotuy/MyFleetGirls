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
  new FinagleProxy(ClientConfig.kcUrl, ClientConfig.proxyPort, new KCIntercepter).start()
}
