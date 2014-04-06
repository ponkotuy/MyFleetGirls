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
  def postUrl(ver: Int = 1) = config.getString("url.post") + s"/post/v${ver}"
  def getUrl(ver: Int = 1) = config.getString("url.post") + s"/rest/v${ver}"
  val proxyPort = config.getInt("proxy.port")
}
