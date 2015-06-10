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
      val sound = allRead(q.response.getContent)
      FilePostable(s"/mp3/kc/${shipKey}/${soundId}/${ver}", "sound", 2, sound, "mp3")
    }.toList
  }
}

case class SoundUrlId(shipKey: String, soundId: Int, version: Int)

object SoundUrlId {
  val pattern = """.*/kcs/sound/kc([a-z]+)/(\d+).mp3\?version\=(\d+)""".r

  def parseURL(url: String): Option[SoundUrlId] = {
    url match {
      case pattern(ship, sound, version) => Try { SoundUrlId(ship, sound.toInt, version.toInt) }.toOption
      case _ => println(s"fail parseURL: ${url}"); None
    }
  }
}
