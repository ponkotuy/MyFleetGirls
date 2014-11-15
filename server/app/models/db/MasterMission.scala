package models.db

import com.ponkotuy.data.master
import scalikejdbc.{DBSession, WrappedResultSet, _}
import util.scalikejdbc.BulkInsert._

/**
 *
 * @author ponkotuy
 * Date 14/03/09.
 */
case class MasterMission(id: Int, mapArea: Int, name: String, time: Int, fuel: Double, ammo: Double)

object MasterMission extends SQLSyntaxSupport[MasterMission] {
  def apply(x: SyntaxProvider[MasterMission])(rs: WrappedResultSet): MasterMission = apply(x.resultName)(rs)
  def apply(x: ResultName[MasterMission])(rs: WrappedResultSet): MasterMission = new MasterMission(
    rs.int(x.id),
    rs.int(x.mapArea),
    rs.string(x.name),
    rs.int(x.time),
    rs.double(x.fuel),
    rs.double(x.ammo)
  )

  lazy val mm = MasterMission.syntax("mm")

  def findAll()(implicit session: DBSession = MasterMission.autoSession): List[MasterMission] = withSQL {
    select.from(MasterMission as mm)
  }.map(MasterMission(mm)).toList().apply()

  def count()(implicit session: DBSession = MasterMission.autoSession): Long = withSQL {
    select(sqls"count(1)").from(MasterMission as mm)
  }.map(rs => rs.long(1)).single().apply().get

  def bulkInsert(xs: Seq[master.MasterMission])(implicit session: DBSession = MasterMission.autoSession): Seq[MasterMission] = {
    applyUpdate {
      insert.into(MasterMission)
        .columns(column.id, column.mapArea, column.name, column.time, column.fuel, column.ammo)
        .multiValues(xs.map(_.id), xs.map(_.mapArea), xs.map(_.name), xs.map(_.time), xs.map(_.fuel), xs.map(_.ammo))
    }
    xs.map { x =>
      MasterMission(x.id, x.mapArea, x.name, x.time, x.fuel, x.ammo)
    }
  }

  def deleteByMapArea(mapArea: Int)(implicit session: DBSession = MasterMission.autoSession): Unit = applyUpdate {
    delete.from(MasterMission).where.eq(MasterMission.column.mapArea, mapArea)
  }

  def deleteAll()(implicit session: DBSession = autoSession): Unit = applyUpdate {
    delete.from(MasterMission)
  }
}
