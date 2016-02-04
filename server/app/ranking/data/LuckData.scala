package ranking.data

import org.json4s._
import org.json4s.native.Serialization
import ranking.common.RankingData

import scala.xml.Elem

case class LuckData(ship: ShipMini, luckMin: Int, luck: Int) extends RankingData {
  override def toElem: Elem = <span>+{luck - luckMin}<small>{ship.name}{luckMin}→{luck}</small></span>
}

object LuckData {
  implicit val formats = Serialization.formats(NoTypeHints)
  def decode(v: JValue): Option[LuckData] = v.extractOpt[LuckData]
}
