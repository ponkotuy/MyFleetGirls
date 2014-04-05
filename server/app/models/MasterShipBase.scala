package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{WrappedResultSet, DBSession}
import com.ponkotuy.data.master

/**
 *
 * @author ponkotuy
 * Date: 14/02/25.
 */
case class MasterShipBase(id: Int, name: String, yomi: String, sortno: Int, stype: Int, ctype: Int, cnum: Int)

object MasterShipBase extends SQLSyntaxSupport[MasterShipBase] {
  override val tableName = "master_ship"
  def apply(x: SyntaxProvider[MasterShipBase])(rs: WrappedResultSet): MasterShipBase = apply(x.resultName)(rs)
  def apply(x: ResultName[MasterShipBase])(rs: WrappedResultSet): MasterShipBase = new MasterShipBase(
    rs.int(x.id),
    rs.string(x.name),
    rs.string(x.yomi),
    rs.int(x.sortno),
    rs.int(x.stype),
    rs.int(x.ctype),
    rs.int(x.cnum)
  )

  lazy val ms = MasterShipBase.syntax("ms")

  def findAll()(implicit session: DBSession = MasterShipBase.autoSession): List[MasterShipBase] = withSQL {
    select.from(MasterShipBase as ms)
  }.map(MasterShipBase(ms)).toList().apply()

  def findAllByLike(q: String)(implicit session: DBSession = autoSession): List[MasterShipBase] = withSQL {
    select.from(MasterShipBase as ms).where.like(ms.name, q)
  }.map(MasterShipBase(ms)).toList().apply()

  def count()(implicit session: DBSession = MasterShipBase.autoSession): Long = withSQL {
    select(sqls"count(1)").from(MasterShipBase as ms)
  }.map(rs => rs.long(1)).single().apply().get

  def create(ms: master.MasterShipBase)(implicit session: DBSession = MasterShipBase.autoSession): MasterShipBase = {
    withSQL {
      insert.into(MasterShipBase).namedValues(
        column.id -> ms.id, column.name -> ms.name, column.yomi -> ms.yomi,
        column.sortno -> ms.sortno, column.stype -> ms.stype, column.ctype -> ms.ctype, column.cnum -> ms.cnum
      )
    }.update().apply()
    MasterShipBase(ms.id, ms.name, ms.yomi, ms.sortno, ms.stype, ms.ctype, ms.cnum)
  }

  def deleteAll()(implicit session: DBSession = MasterShipBase.autoSession): Unit =
    withSQL { delete.from(MasterShipBase) }.update().apply()
}
