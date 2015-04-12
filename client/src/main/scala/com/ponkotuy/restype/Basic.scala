package com.ponkotuy.restype

import com.ponkotuy.config.ClientConfig
import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.JsonAST.JValue
import org.json4s.native.Serialization._

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object Basic extends ResType {
  import ResType._

  private[restype] var memberId: Option[Long] = None

  override def regexp: Regex = s"\\A$GetMember/basic\\z".r

  override def postables(q: Query): Seq[Result] = postablesFromObj(q.obj)

  def postablesFromObj(obj: JValue): Seq[Result] = {
    val auth = data.Auth.fromJSON(obj)
    memberId = Some(auth.memberId)
    val auth2 = ClientConfig.auth(auth.memberId)

    val basic = data.Basic.fromJSON(obj)
    NormalPostable("/basic", write(basic), 1, basic.summary) :: Authentication(auth, auth2) :: Nil
  }
}
