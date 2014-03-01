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
  val Ponkotu = 110136878L

  val GetMaster = "/kcsapi/api_get_master"
  val GetMember = "/kcsapi/api_get_member"
  private[this] var auth: Option[Auth] = None

  case object Material extends ResType(s"$GetMember/material") {
    override def run(reqHeaders: Map[String, String], obj: JValue): Unit = {
      val material = data.Material.fromJson(obj)
      post("/material", write(material))
    }
  }

  case object Basic extends ResType(s"$GetMember/basic") {
    override def run(reqHeaders: Map[String, String], obj: JValue): Unit = {
      auth = Some(data.Auth.fromJSON(obj))
      val basic = data.Basic.fromJSON(obj)
      post("/basic", write(basic))
    }
  }

  case object Ship3 extends ResType(s"$GetMember/ship3") {
    override def run(reqHeaders: Map[String, String], obj: JValue): Unit = {
      val ship = data.Ship.fromJson(obj \ "api_ship_data")
      post("/ship", write(ship))
    }
  }

  case object NDock extends ResType(s"$GetMember/ndock") {
    def run(reqHeaders: Map[String, String], obj: JValue): Unit = {
      val docks = data.NDock.fromJson(obj)
      info(docks)
    }
  }

  case object MasterShip extends ResType(s"$GetMaster/ship") {
    override def run(reqHeaders: Map[String, String], obj: JValue): Unit = {
      if(auth.map(_.id) == Some(Ponkotu)) {
        val ships = data.MasterShip.fromJson(obj)
        post("/master/ship", write(ships))
      }
    }
  }

  val values = Set(Material, Basic, Ship3, NDock, MasterShip)

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
