package models.db

import scalikejdbc._
import scalikejdbc._
import com.ponkotuy.data
import util.scalikejdbc.BulkInsert._

case class MapInfo(
  memberId: Long,
  id: Int,
  cleared: Boolean,
  exbossFlag: Boolean) {

  def save()(implicit session: DBSession = MapInfo.autoSession): MapInfo = MapInfo.save(this)(session)

  def destroy()(implicit session: DBSession = MapInfo.autoSession): Unit = MapInfo.destroy(this)(session)

  def abbr: String = s"${id/10}-${id%10}"

}


object MapInfo extends SQLSyntaxSupport[MapInfo] {

  override val tableName = "map_info"

  override val columns = Seq("member_id", "id", "cleared", "exboss_flag")

  def apply(mi: ResultName[MapInfo])(rs: WrappedResultSet): MapInfo = autoConstruct(rs, mi)

  lazy val mi = MapInfo.syntax("mi")

  override val autoSession = AutoSession

  def find(id: Int, memberId: Long)(implicit session: DBSession = autoSession): Option[MapInfo] = {
    withSQL {
      select.from(MapInfo as mi).where.eq(mi.id, id).and.eq(mi.memberId, memberId)
    }.map(MapInfo(mi.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MapInfo] = {
    withSQL(select.from(MapInfo as mi)).map(MapInfo(mi.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MapInfo as mi)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MapInfo] = {
    withSQL {
      select.from(MapInfo as mi).where.append(sqls"${where}")
    }.map(MapInfo(mi.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MapInfo as mi).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    memberId: Long,
    id: Int,
    cleared: Boolean,
    exbossFlag: Boolean)(implicit session: DBSession = autoSession): MapInfo = {
    withSQL {
      insert.into(MapInfo).columns(
        column.memberId,
        column.id,
        column.cleared,
        column.exbossFlag
      ).values(
          memberId,
          id,
          cleared,
          exbossFlag
        )
    }.update().apply()

    MapInfo(
      memberId = memberId,
      id = id,
      cleared = cleared,
      exbossFlag = exbossFlag)
  }

  def bulkInsert(xs: Seq[data.MapInfo], memberId: Long)(implicit session: DBSession = autoSession): Seq[MapInfo] = {
    applyUpdate {
      insert.into(MapInfo)
        .columns(column.memberId, column.id, column.cleared, column.exbossFlag)
        .multiValues(Seq.fill(xs.size)(memberId), xs.map(_.id), xs.map(_.cleared), xs.map(_.exbossFlag))
    }
    xs.map { x => MapInfo(memberId, x.id, x.cleared, x.exbossFlag) }
  }

  def save(entity: MapInfo)(implicit session: DBSession = autoSession): MapInfo = {
    withSQL {
      update(MapInfo).set(
        column.memberId -> entity.memberId,
        column.id -> entity.id,
        column.cleared -> entity.cleared,
        column.exbossFlag -> entity.exbossFlag
      ).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId)
    }.update().apply()
    entity
  }

  def destroy(entity: MapInfo)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MapInfo).where.eq(column.id, entity.id).and.eq(column.memberId, entity.memberId)
    }.update().apply()
  }

  def deleteAllByUser(memberId: Long)(implicit session: DBSession = autoSession): Unit = applyUpdate {
    delete.from(MapInfo).where.eq(MapInfo.column.memberId, memberId)
  }

}
