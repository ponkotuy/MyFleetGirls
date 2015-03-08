package models.response

import models.join.CellWithRank
import org.json4s._
import org.json4s.JsonDSL._

/**
 * @author ponkotuy
 * Date: 15/03/08.
 */
case class DropRate(cell: CellWithRank, count: Long, sum: Long) {
  def rate: Double = count.toDouble / sum
  def toJson: JObject = ("cell" -> cell.toJson) ~
      ("count" -> count) ~
      ("sum" -> sum)
}
