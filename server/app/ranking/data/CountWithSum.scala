package ranking.data

import org.json4s._
import org.json4s.native.Serialization
import ranking.common.RankingData

import scala.xml.Elem

case class CountWithSum(count: Long, sum: Long) extends RankingData {
  override def toElem: Elem = <span><strong>{f"$count%,d"}</strong>{s" / $sum"}</span>
}

object CountWithSum {
  implicit val formats = Serialization.formats(NoTypeHints)
  def decode(v: JValue): Option[CountWithSum] = v.extractOpt[CountWithSum]
}
