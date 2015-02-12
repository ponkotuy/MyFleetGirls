package models.req

import java.util.Locale

import play.api.data.Form
import play.api.data.Forms._

/**
 *
 * @author ponkotuy
 * Date: 15/02/12.
 */
case class AllCrawlAPI(sort: SortType, fromId: Option[Long])

object AllCrawlAPI {
  def fromForm(sort: Option[String], fromId: Option[Long]): AllCrawlAPI =
    AllCrawlAPI(sort.flatMap(SortType.fromStr).getOrElse(SortType.Normal), fromId)

  def toForm(api: AllCrawlAPI): Option[(Option[String], Option[Long])] =
    Some((Some(api.sort.toString), api.fromId))

  val form = Form(mapping("sort" -> optional(text), "from_id" -> optional(longNumber))(AllCrawlAPI.fromForm)(AllCrawlAPI.toForm))
}

sealed abstract class SortType

object SortType {
  case object Normal extends SortType // APIの初期仕様の従う
  case object Id extends SortType // ID順にSort

  val values = Vector(Normal, Id)
  def fromStr(str: String) = values.find(_.toString.toLowerCase(Locale.US) == str)
}
