package ranking.data

import models.join.ShipParameter
import org.json4s._
import org.json4s.native.Serialization
import ranking.common.RankingData

import scala.xml.Elem

case class ShipMini(masterId: Int, id: Int, name: String, lv: Short) extends RankingData {
  override def toElem: Elem = <span>{name}<small>{s"Lv${lv}"}</small></span>
}

object ShipMini {
  implicit val formats = Serialization.formats(NoTypeHints)

  def toData(ship: ShipParameter): ShipMini =
    ShipMini(ship.master.id, ship.ship.id, ship.name, ship.lv)

  def decode(v: JValue): Option[ShipMini] = v.extractOpt[ShipMini]
}
