package com.ponkotuy.restype

import com.ponkotuy.data.{Auth, MyFleetAuth}
import com.ponkotuy.util.Log

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
sealed trait Result
case class Authentication(auth: Auth, auth2: Option[MyFleetAuth]) extends Result

sealed trait HttpPostable extends Result {
  import HttpPostable.logger

  def message: String
  def printMessage(): Unit = {
    if(message.nonEmpty) {
      println(message)
      logger.debug(message)
    }
  }
}

object HttpPostable extends Log

case class NormalPostable(url: String, data: String, ver: Int = 1, message: String = "") extends HttpPostable
case class MasterPostable(url: String, data: String, ver: Int = 1, message: String = "") extends HttpPostable
case class FilePostable(url: String, fileBodyKey: String, ver: Int, file: Array[Byte], ext: String, message: String = "") extends HttpPostable
