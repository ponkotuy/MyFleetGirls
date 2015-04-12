package com.ponkotuy.restype

import com.ponkotuy.http.MFGHttp
import com.ponkotuy.parser.{Query, SoundUrlId}
import com.ponkotuy.restype.ShipSWF.allRead

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
