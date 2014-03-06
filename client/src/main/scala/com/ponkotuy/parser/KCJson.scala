package com.ponkotuy.parser

import org.json4s._
import org.json4s.native.JsonMethods._
import scala.util.Try
import org.json4s.native.Serialization

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
object KCJson {
  implicit val formats = Serialization.formats(NoTypeHints)

  def toAst(content: String): Option[JValue] = {
    Try {
      val ast = parse(content.replaceFirst("svdata=", ""))
      assert((ast \ "api_result").extract[Int] == 1)
      ast \ "api_data"
    }.toOption
  }
}
