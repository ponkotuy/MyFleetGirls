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
    SoundUrlId.parseURL(q.toString).filterNot(MFGHttp.existsSound).map { case SoundUrlId(shipKey, soundId) =>
      val sound = allRead(q.response.getContent)
      FilePostable(s"/mp3/kc/${shipKey}/${soundId}", "sound", 1, sound, "mp3")
    }.toList
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
