package com.ponkotuy.config

import java.io.File
import com.typesafe.config.ConfigFactory

/**
 *
 * @author ponkotuy
 * Date: 14/02/23
 */
object ClientConfig {
  val config = ConfigFactory.parseFile(new File("application.conf"))

  val kcUrl = config.getString("url.kc")
  val postUrl = config.getString("url.post") + "/post/v1"
  val getUrl = config.getString("url.post") + "/rest/v1"
  val proxyPort = config.getInt("proxy.port")
}
