package ranking.data

import org.json4s._
import org.json4s.native.Serialization
import ranking.common.RankingData

import scala.xml.Elem

case class Exp(exp: Long) extends RankingData {
  override def toElem: Elem = <span>{f"$exp%,d"}</span>
}

object Exp {
  implicit val formats = Serialization.formats(NoTypeHints)
  def decode(v: JValue): Option[Exp] = v.extractOpt[Exp]
}

