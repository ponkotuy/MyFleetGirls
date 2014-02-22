package com.ponkotuy.parser

import scala.concurrent.ExecutionContext.Implicits._
import com.github.theon.uri.Uri
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import dispatch._
import com.ponkotuy.data
import com.ponkotuy.util.Log
import com.ponkotuy.data.Auth
import com.ponkotuy.config.ClientConfig
import java.nio.charset.Charset

/**
 *
 * @author ponkotuy
 * Date: 14/02/19.
 */
sealed abstract class ResType(val path: String) {
  def run(reqHeaders: Map[String, String], obj: JValue): Unit
}

object ResType extends Log {
  implicit val formats = Serialization.formats(NoTypeHints)

  val GetMember = "/kcsapi/api_get_member"
  private[this] var auth: Option[Auth] = None

  case object Material extends ResType(s"$GetMember/material") {
    override def run(reqHeaders: Map[String, String], obj: JValue): Unit = {
      val xs: Seq[Int] = (obj \\ "api_value").children.map(_.extract[Int])
      val material = data.Material.fromSeq(xs)
      post("/material", write(material))
      info(material)
    }
  }

  case object Basic extends ResType(s"$GetMember/basic") {
    override def run(reqHeaders: Map[String, String], obj: JValue): Unit = {
      auth = Some(data.Auth.fromJSON(obj))
      val basic = data.Basic.fromJSON(obj)
      post("/basic", write(basic))
      info(basic, auth)
    }
  }

  val values = Set(Material, Basic)

  def post(uStr: String, data: String) = {
    Http(url(ClientConfig.postUrl + uStr) << Map("auth" -> write(auth), "data" -> data)).either.foreach {
      case Left(e) => error("POST Error"); error(e)
      case Right(res) => info(s"POST Success: ($uStr, $data)\n${res.getResponseBody("UTF-8")}")
    }
  }

  def fromUri(uri: String): Option[ResType] = {
    val path = Uri.parseUri(uri).pathRaw
    println(path)
    values.find(_.path == path)
  }
}
