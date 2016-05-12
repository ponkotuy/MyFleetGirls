package com.ponkotuy.restype

import com.ponkotuy.http.MFGHttp
import com.ponkotuy.parser.Query
import com.ponkotuy.restype.ShipSWF.allRead

import scala.util.Try
import scala.util.matching.Regex

/**
 * @author ponkotuy
 * DAte: 15/04/12.
 */
case object SoundMP3 extends ResType {
  override def regexp: Regex = """\A/kcs/sound/kc[a-z]+/[0-9]+\.mp3""".r

  override def postables(q: Query): Seq[Result] = {
    SoundUrlId.parseURL(q.uri.toString())
        .filterNot(MFGHttp.existsSound)
        .map { case SoundUrlId(shipKey, soundId, ver) =>
      val sound = allRead(q.responseContent)
      FilePostable(s"/mp3/kc/${shipKey}/${soundId}/${ver}", "sound", 2, sound, "mp3")
    }.toList
  }
}

case class SoundUrlId(shipKey: String, soundId: Int, version: Int)

object SoundUrlId {
  val Pattern = """.*/kcs/sound/kc([a-z]+)/(\d+).mp3\?version\=(\d+)""".r
  val NoVerPattern = """.*/kcs/sound/kc([a-z]+)/(\d+).mp3""".r

  def parseURL(url: String): Option[SoundUrlId] = {
    url match {
      case Pattern(ship, sound, version) => Try { SoundUrlId(ship, sound.toInt, version.toInt) }.toOption
      case NoVerPattern(ship, sound) => Try { SoundUrlId(ship, sound.toInt, 0) }.toOption
      case _ => println(s"fail parseURL: ${url}"); None
    }
  }
}
