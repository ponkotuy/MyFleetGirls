package com.ponkotuy.tool

import java.net.URLDecoder

import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 15/01/31.
 */
object PostQueryParser {
  val UTF8 = "UTF-8"

  def parse(str: String): Map[String, String] =
    Try {
      URLDecoder.decode(str, UTF8).split('&').map { elem =>
        val ary = elem.split('=')
        ary.head -> ary.tail.headOption.getOrElse("")
      }.toMap
    }.getOrElse(Map())
}
