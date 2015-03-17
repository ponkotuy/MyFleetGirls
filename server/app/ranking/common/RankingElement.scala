package ranking.common

import scala.xml.Elem

/**
 *
 * @author ponkotuy
 * Date: 15/01/29.
 */
@SerialVersionUID(1L)
case class RankingElement(name: String, value: Elem, url: Option[String], num: Long)

object RankingElement {
  def apply(name: String, value: Elem, num: Long): RankingElement = RankingElement(name, value, None, num)
  def apply(name: String, value: Elem, url: String, num: Long): RankingElement =
    RankingElement(name, value, Some(url), num)
}
