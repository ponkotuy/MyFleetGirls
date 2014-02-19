package com.ponkotuy.run

import com.ponkotuy.intercept.{KCIntercepter, PrintContent}
import com.ponkotuy.proxy.FinagleProxy

/**
 *
 * @author ponkotuy
 * Date: 14/02/18.
 */
object Main extends App {
  new FinagleProxy("125.6.189.39", new KCIntercepter).start()
}
