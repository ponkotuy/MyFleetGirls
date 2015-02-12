package models.join

import models.db.{CellInfo, BattleResult}
import org.json4s._
import org.json4s.JsonDSL._
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/05/06.
 */
case class CellWithRank(area: Int, info: Int, cell: Int, rank: String, alpha: Option[String]) {
  lazy val stage = Stage(area, info)

  def viewName = stage.toString

  def toJson: JObject = Extraction.decompose(this)(DefaultFormats).asInstanceOf[JObject] ~ ("viewName" -> viewName)
}

object CellWithRank {
  def apply(br: SyntaxProvider[BattleResult], ci: SyntaxProvider[CellInfo])(rs: WrappedResultSet): CellWithRank = new CellWithRank(
    rs.int(br.areaId),
    rs.int(br.infoNo),
    rs.int(br.cell),
    rs.string(br.winRank),
    rs.stringOpt(ci.alphabet)
  )
}
