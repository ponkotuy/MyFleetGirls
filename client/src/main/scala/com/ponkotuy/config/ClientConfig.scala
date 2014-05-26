package com.ponkotuy.config

import java.io.File
import scala.util.Try
import com.typesafe.config.ConfigFactory
import com.ponkotuy.data.MyFleetAuth

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

  @deprecated("Move to Auth.master", "0.13.0")
  def master: Boolean = Auth.master
  def auth(memberId: Long): Option[MyFleetAuth] = Auth.password.map(p => MyFleetAuth(memberId, p))

  object Auth {
    val conf = config.getConfig("auth")
    val master: Boolean = Try { conf.getBoolean("master") }.getOrElse(false)
    val password: Option[String] = Try { conf.getString("pass") }.toOption
  }
}
