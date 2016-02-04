package ranking.data

import org.json4s._
import org.json4s.native.Serialization
import ranking.common.RankingData

import scala.xml.Elem

case class Score(score: Int) extends RankingData {
  override def toElem: Elem = <span>{f"$score%,d"}</span>
}

object Score {
  implicit val formats = Serialization.formats(NoTypeHints)
  def decode(v: JValue): Option[Score] = v.extractOpt[Score]
}

