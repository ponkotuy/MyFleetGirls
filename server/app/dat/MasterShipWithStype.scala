package dat

import scalikejdbc.SQLInterpolation._
import models.{MasterStype, MasterShipBase}
import scalikejdbc.WrappedResultSet

/**
 *
 * @author ponkotuy
 * Date: 14/04/20.
 */
case class MasterShipWithStype(ms: MasterShipBase, stype: MasterStype) {
  def shipId = ms.id
  def name = ms.name
  def stypeId = ms.stype
  def ctype = ms.ctype
  def cnum = ms.cnum
  def stypeName = stype.name
}

object MasterShipWithStype {
  def apply(ms: SyntaxProvider[MasterShipBase], msb: SyntaxProvider[MasterStype])(rs:WrappedResultSet): MasterShipWithStype =
    apply(ms.resultName, msb.resultName)(rs)
  def apply(ms: ResultName[MasterShipBase], mst: ResultName[MasterStype])(rs: WrappedResultSet): MasterShipWithStype =
    new MasterShipWithStype(MasterShipBase(ms)(rs), MasterStype(mst)(rs))
}
