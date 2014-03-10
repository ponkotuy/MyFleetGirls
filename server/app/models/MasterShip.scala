package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{WrappedResultSet, DBSession}
import com.ponkotuy.data.master

/**
 *
 * @author ponkotuy
 * Date: 14/02/25.
 */
case class MasterShip(id: Int, name: String, yomi: String)

object MasterShip extends SQLSyntaxSupport[MasterShip] {
  def apply(x: SyntaxProvider[MasterShip])(rs: WrappedResultSet): MasterShip = apply(x.resultName)(rs)
  def apply(x: ResultName[MasterShip])(rs: WrappedResultSet): MasterShip = new MasterShip(
    rs.int(x.id),
    rs.string(x.name),
    rs.string(x.yomi)
  )

  lazy val ms = MasterShip.syntax("ms")

  def findAll()(implicit session: DBSession = MasterShip.autoSession): List[MasterShip] = withSQL {
    select.from(MasterShip as ms)
  }.map(MasterShip(ms)).toList().apply()

  def count()(implicit session: DBSession = MasterShip.autoSession): Long = withSQL {
    select(sqls"count(1)").from(MasterShip as ms)
  }.map(rs => rs.long(1)).single().apply().get

  def create(ms: master.MasterShip)(implicit session: DBSession = MasterShip.autoSession): MasterShip = {
    withSQL {
      insert.into(MasterShip).namedValues(
        column.id -> ms.id, column.name -> ms.name, column.yomi -> ms.yomi
      )
    }.update().apply()
    MasterShip(ms.id, ms.name, ms.yomi)
  }

  def deleteAll()(implicit session: DBSession = MasterShip.autoSession): Unit =
    withSQL { delete.from(MasterShip) }.update().apply()
}
