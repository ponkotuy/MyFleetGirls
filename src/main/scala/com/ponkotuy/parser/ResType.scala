package com.ponkotuy.parser

import com.github.theon.uri.Uri
import org.json4s._
import org.json4s.native.Serialization
import com.ponkotuy.data


/**
 *
 * @author ponkotuy
 * Date: 14/02/19.
 */
sealed abstract class ResType(val path: String) {
  def run(reqHeaders: Map[String, String], obj: JValue): Unit
}

object ResType {
  implicit val formats = Serialization.formats(NoTypeHints)

  val GetMember = "/kcsapi/api_get_member"

  case object Material extends ResType(s"$GetMember/material") {
    override def run(reqHeaders: Map[String, String], obj: JValue): Unit = {
      val xs: Seq[Int] = (obj \\ "api_value").children.map(_.extract[Int])
      val material = data.Material.fromSeq(xs)
      println(material)
    }
  }

  case object Basic extends ResType(s"$GetMember/basic") {
    override def run(reqHeaders: Map[String, String], obj: JValue): Unit = {
      val basic = data.Basic.fromJSON(obj)
      val auth = data.Auth.fromJSON(obj)
      println(basic, auth)
    }
  }

  val values = Set(Material, Basic)

  def fromUri(uri: String): Option[ResType] = {
    val path = Uri.parseUri(uri).pathRaw
    println(path)
    values.find(_.path == path)
  }
}
