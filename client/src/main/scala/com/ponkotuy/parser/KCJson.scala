package com.ponkotuy.parser

import com.ponkotuy.util.Log
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization

/**
 *
 * @author ponkotuy
 * Date 14/02/19.
 */
object KCJson extends Log {
  implicit val formats = Serialization.formats(NoTypeHints)

  def toAst(content: String): Either[Exception, JValue] = {
    try {
      val extracted = content.dropWhile(_ != '{')
      val ast = parse(extracted)
      if((ast \ "api_result").extract[Int] == 1) Right(ast \ "api_data")
      else {
        val JString(message) = ast \ "api_message"
        error(s"Returned error from KanColle server. message=${message}")
        Left(new KanColleAPIException(message))
      }
    } catch {
      case e: Exception =>
        error("JSON Parse error.", e)
        Left(e)
    }
  }
}

/** 艦これAPIがエラーを返した */
class KanColleAPIException(message: String) extends Exception(message)
