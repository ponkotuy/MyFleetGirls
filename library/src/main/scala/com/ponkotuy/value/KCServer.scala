package com.ponkotuy.value

import scala.collection.JavaConversions._
import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.{Logger, LoggerFactory}

/**
 * Date: 14/06/10
 */
case class KCServer(number: Int, ip: String, name: String)

object KCServer {
  val configName = "kc-server"
  lazy val logger: Logger = LoggerFactory.getLogger(getClass)
  lazy val config: Config = {
    logger.info(s"config file loading. resource: ${configName}")
    ConfigFactory.load(configName);
  }

  lazy val values = {
    logger.info("load servers list")
    val configList = config.getConfigList("servers").toList
    val serverList = configList.map { config =>
      val number = config.getInt("number")
      val ip = config.getString("ip")
      val name = config.getString("name")
      logger.debug(s"add server. number: ${number}, ip: ${ip}, name: ${name}")
      KCServer(number,ip,name)
    }
    serverList
  }

  lazy val ips = values.map(_.ip).toSet

  def list(): List[KCServer] = values
  def fromNumber(number: Int): Option[KCServer] = values.find(_.number == number)
  def fromIP(ip: String): Option[KCServer] = values.find(_.ip == ip)
}

