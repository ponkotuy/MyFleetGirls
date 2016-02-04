package ranking.common

import scala.xml.Elem

/**
 *
 * @author ponkotuy
 * Date: 15/01/29.
 */
@SerialVersionUID(2L)
case class RankingElement(targetId: Long, name: String, data: RankingData, url: Option[String], num: Long) {
  def value = data.toString
}

object RankingElement {
  def apply(targetId: Long, name: String, data: RankingData, num: Long): RankingElement =
    RankingElement(targetId, name, data, None, num)
  def apply(targetId: Long, name: String, data: RankingData, url: String, num: Long): RankingElement =
    RankingElement(targetId, name, data, Some(url), num)
}

// JSONにSerializeできる必要がある
trait RankingData {
  def toElem: Elem
}
