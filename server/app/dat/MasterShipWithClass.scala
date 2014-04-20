package dat

import scalikejdbc.SQLInterpolation._
import models.MasterShipBase
import scalikejdbc.WrappedResultSet
import scala.util.Try

/**
 *
 * @author ponkotuy
 * Date: 14/04/20.
 */
case class MasterShipWithClass(ms: MasterShipBase, lead: Option[MasterShipBase]) {
  def shipId = ms.id
  def name = ms.name
  def stype = ms.stype
  def ctype = ms.ctype
  def cnum = ms.cnum
  lazy val cls = lead.map(_.name + "型").getOrElse(name)
}

object MasterShipWithClass {
  def apply(ms: SyntaxProvider[MasterShipBase], msb: SyntaxProvider[MasterShipBase])(rs:WrappedResultSet): MasterShipWithClass =
    apply(ms.resultName, msb.resultName)(rs)
  def apply(ms: ResultName[MasterShipBase], mst: ResultName[MasterShipBase])(rs: WrappedResultSet): MasterShipWithClass =
    new MasterShipWithClass(MasterShipBase(ms)(rs), Try { MasterShipBase(mst)(rs) }.toOption)
}
