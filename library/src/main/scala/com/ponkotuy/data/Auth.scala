package com.ponkotuy.data

import org.json4s.JValue
import org.json4s.JsonAST.JString

/** このツール内でログイン代わりに使うパラメータ
  *
  * @param id nick name id
 */
case class Auth(id: Long, nickname: String)

object Auth {
  def fromJSON(json: JValue): Auth = {
    val JString(id) = json \ "api_nickname_id"
    val JString(nickname) = json \ "api_nickname"
    Auth(id.toLong, nickname)
  }
}
