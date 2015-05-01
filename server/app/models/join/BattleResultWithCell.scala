package models.join

import models.db.{BattleResult, CellInfo}
import org.json4s.JsonDSL._
import org.json4s._
import scalikejdbc._

import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/04/29.
 */
case class BattleResultWithCell(br: BattleResult, ci: Option[CellInfo]) {
  def areaId: Int = br.areaId
  def infoNo: Int = br.infoNo
  def cell: Int = br.cell
  def alphabet: Option[String] = ci.map(_.alphabet)
  def winRank: String = br.winRank
  def created: Long = br.created

  lazy val stage = Stage(areaId, infoNo)

  def toJson: JObject = {
    implicit val defaults = DefaultFormats
    val alpha = alphabet.map(it => s"(${it})").getOrElse("")
    Extraction.decompose(br).asInstanceOf[JObject] ~
      ci.map(it => Extraction.decompose(it).asInstanceOf[JObject]).getOrElse(JObject()) ~
      ("point" -> s"$stage-$cell $alpha")
  }
}

object BattleResultWithCell {
  def apply(br: SyntaxProvider[BattleResult], ci: SyntaxProvider[CellInfo])(rs: WrappedResultSet): BattleResultWithCell =
    apply(br.resultName, ci.resultName)(rs)
  def apply(br: ResultName[BattleResult], ci: ResultName[CellInfo])(rs: WrappedResultSet): BattleResultWithCell =
    BattleResultWithCell(BattleResult(br)(rs), Try { CellInfo(ci)(rs) }.toOption)
}
