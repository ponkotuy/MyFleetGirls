package ranking.data

import org.json4s._
import org.json4s.native.Serialization
import ranking.common.RankingData

import scala.xml.Elem

case class Count(count: Long) extends RankingData {
  override def toElem: Elem = <span>{f"$count%,d"}</span>
}

object Count {
  implicit val formats = Serialization.formats(NoTypeHints)
  def decode(v: JValue): Option[Count] = v.extractOpt[Count]
}
