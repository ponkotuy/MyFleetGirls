package com.ponkotuy.data

import org.json4s.JValue
import org.json4s.JsonAST.JString

/** このツール内でログイン代わりに使うパラメータ
  *
  * @param id nick name id
  * @param memberId KanColle ID
  */
case class Auth(id: Long, nickname: String, memberId: Long)

object Auth {
  def fromJSON(json: JValue): Auth = {
    try {
      val JString(id) = json \ "api_nickname_id"
      val JString(nickname) = json \ "api_nickname"
      val JString(memberId) = json \ "api_member_id"
      Auth(id.toLong, nickname, memberId.toLong)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException("ログイン情報取得エラー。間違えて他のマシンから繋いじゃったとか？")
    }
  }
}
