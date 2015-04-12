package com.ponkotuy.parser

import com.ponkotuy.data._
import com.ponkotuy.http.MFGHttp
import com.ponkotuy.util.Log
import com.ponkotuy.value.KCServer
import org.json4s._
import org.json4s.native.Serialization.write

import scala.util.Try

/**
 * Date: 14/06/01.
 */
object Post extends Log {
  implicit val formats = DefaultFormats

  def admiralSettings(kcServer: KCServer)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
    MFGHttp.post("/admiral_settings", write(kcServer))
    println(s"所属： ${kcServer.name}")
  }

  def mp3kc(q: Query)(implicit auth: Option[Auth], auth2: Option[MyFleetAuth]): Unit = {
  }
}

case class SoundUrlId(shipKey: String, soundId: Int)

object SoundUrlId {
  val pattern = """.*/kcs/sound/kc([a-z]+)/(\d+).mp3""".r

  def parseURL(url: String): Option[SoundUrlId] = {
    url match {
      case pattern(ship, sound) => Try { SoundUrlId(ship, sound.toInt) }.toOption
      case _ => None
    }
  }
}
