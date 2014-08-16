package com.ponkotuy.parser

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
object KCJson {
  implicit val formats = Serialization.formats(NoTypeHints)

  def toAst(content: String): Either[JValue, String] = {
    try {
      val extracted = content.dropWhile(_ != '{')
      val ast = parse(extracted)
      if((ast \ "api_result").extract[Int] == 1) Left(ast \ "api_data")
      else {
        val JString(message) = ast \ "api_message"
        Right(message)
      }
    } catch {
      case e: Exception => Right(e.getMessage)
    }
  }
}
