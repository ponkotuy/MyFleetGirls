package com.ponkotuy.restype

import com.netaporter.uri.Uri
import com.ponkotuy.config.ClientConfig
import com.ponkotuy.data
import com.ponkotuy.data.{Auth, MyFleetAuth}
import com.ponkotuy.http.MFGHttp
import com.ponkotuy.parser.Query
import com.ponkotuy.value.KCServer
import org.json4s._
import org.json4s.native.Serialization._

import scala.util.matching.Regex

class Basic extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/basic\\z".r

  override def postables(q: Query): Seq[Result] = postablesFromObj(q.obj, q.uri)

  private[this] var admiral = Admiral(None, None)

  private[this] var initSending = false

  def postablesFromObj(obj: JValue, uri: Uri): Seq[Result] = {
    val auth = data.Auth.fromJSON(obj)
    if(getAdmiral.memberId.exists(_ != auth.memberId)) {
      System.err.println("異なる艦これアカウントによる通信を検知しました。一旦終了します")
      System.exit(1) // 例外が伝搬するか自信が無かったので問答無用で殺す
    }
    admiral = Admiral(memberId = Some(auth.memberId), nickname = Some(auth.nickname))
    val auth2 = ClientConfig.auth(auth.memberId)

    if(!initSending) postAdmiralSettings(uri)(Some(auth), auth2)

    val basic = data.Basic.fromJSON(obj)

    NormalPostable("/basic", write(basic), 1, basic.summary) :: Authentication(auth, auth2) :: Nil
  }

  private def postAdmiralSettings(uri: Uri)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    for {
      host <- uri.host
      kcServer <- KCServer.fromIP(host)
    } {
      MFGHttp.post("/admiral_settings", write(kcServer))(auth, auth2)
      println(s"所属： ${kcServer.name}")
      initSending = true
    }
  }

  def getAdmiral = admiral
}

case class Admiral(memberId: Option[Long], nickname: Option[String])
