package com.ponkotuy.config

import java.io.{BufferedReader, File, InputStreamReader}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Path}
import java.util.stream.Collectors

import com.ponkotuy.data.MyFleetAuth
import com.typesafe.config.ConfigFactory
import org.apache.http.HttpHost

import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/02/23
 */
object ClientConfig {
  lazy val config = {
    val file = new File("application.conf")
    if (!file.exists()) {
      createDefaultAppConfig(file.toPath)
    }
    ConfigFactory.parseFile(file).withFallback(ConfigFactory.defaultReference())
  }

  private def createDefaultAppConfig(path: Path): Unit = {
    val default = getClass.getResourceAsStream("/reference.conf")
    val reader = new BufferedReader(new InputStreamReader(default, StandardCharsets.UTF_8))
    try
      Files.write(path, reader.lines().collect(Collectors.toList()), Charset.defaultCharset())
    finally
      reader.close()
  }

  lazy val post = config.getString("url.post")
  def postUrl(ver: Int = 1) = post + s"/post/v${ver}"
  def getUrl(ver: Int = 1) = config.getString("url.post") + s"/rest/v${ver}"
  def proxyPort = config.getInt("proxy.port")
  def proxyHost = Try { config.getString("proxy.host") }.getOrElse("localhost")
  lazy val clientProxyHost: Option[HttpHost] = {
    for {
      proxy <- Try { config.getConfig("url.proxy") }.toOption
      port <- Try { proxy.getInt("port") }.toOption
    } yield {
      val host = Try { proxy.getString("host") }.getOrElse("localhost")
      new HttpHost(host, port)
    }
  }

  lazy val upstreamProxyHost: Option[HttpHost] = {
    for {
      upstream <- Try { config.getConfig("upstream_proxy") }.toOption
      port <- Try { upstream.getInt("port") }.toOption
    } yield {
      val host = Try { upstream.getString("host") }.getOrElse("localhost")
      new HttpHost(host, port)
    }
  }

  def auth(memberId: Long): Option[MyFleetAuth] = Auth.password.map(p => MyFleetAuth(memberId, p))

  object Auth {
    val conf = Try { config.getConfig("auth") }.toOption
    val master: Boolean = Try { conf.get.getBoolean("master") }.getOrElse(false)
    val password: Option[String] = Try { conf.get.getString("pass") }.toOption
  }
}
