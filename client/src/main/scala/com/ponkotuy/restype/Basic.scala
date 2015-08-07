package com.ponkotuy.restype

import com.netaporter.uri.Uri
import com.ponkotuy.config.ClientConfig
import com.ponkotuy.data
import com.ponkotuy.data.{MyFleetAuth, Auth}
import com.ponkotuy.http.MFGHttp
import com.ponkotuy.parser.Query
import com.ponkotuy.value.KCServer
import org.json4s._
import org.json4s.native.Serialization._

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object Basic extends ResType {
  import ResType._

  private[restype] var memberId: Option[Long] = None
  private[this] var initSended = false

  override def regexp: Regex = s"\\A$GetMember/basic\\z".r

  override def postables(q: Query): Seq[Result] = postablesFromObj(q.obj, q.uri)

  def postablesFromObj(obj: JValue, uri: Uri): Seq[Result] = {
    val auth = data.Auth.fromJSON(obj)
    memberId = Some(auth.memberId)
    val auth2 = ClientConfig.auth(auth.memberId)

    if(!initSended) postAdmiralSettings(uri)(Some(auth), auth2)

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
      initSended = true
    }
  }
}
