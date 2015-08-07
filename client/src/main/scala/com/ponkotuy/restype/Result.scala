package com.ponkotuy.restype

import com.ponkotuy.data.{Auth, MyFleetAuth}

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
sealed trait Result
case class Authentication(auth: Auth, auth2: Option[MyFleetAuth]) extends Result

sealed trait HttpPostable extends Result {
  def message: String
  def printMessage(): Unit = {
    if(message.nonEmpty) println(message)
  }
}

case class NormalPostable(url: String, data: String, ver: Int = 1, message: String = "") extends HttpPostable
case class MasterPostable(url: String, data: String, ver: Int = 1, message: String = "") extends HttpPostable
case class FilePostable(url: String, fileBodyKey: String, ver: Int, file: Array[Byte], ext: String, message: String = "") extends HttpPostable
