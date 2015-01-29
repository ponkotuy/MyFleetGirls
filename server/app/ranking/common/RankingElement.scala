package ranking.common

import scala.xml.Elem

/**
 *
 * @author ponkotuy
 * Date: 15/01/29.
 */
@SerialVersionUID(1L)
case class RankingElement(name: String, value: Elem, url: Option[String])

object RankingElement {
  def apply(name: String, value: Elem): RankingElement = RankingElement(name, value, None)
  def apply(name: String, value: Elem, url: String): RankingElement = RankingElement(name, value, Some(url))
}
