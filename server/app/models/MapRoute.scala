package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data

case class MapRoute(
  id: Long,
  memberId: Long,
  areaId: Int,
  infoNo: Int,
  dep: Int,
  dest: Int,
  fleet: String,
  created: Long) {

  def save()(implicit session: DBSession = MapRoute.autoSession): MapRoute = MapRoute.save(this)(session)

  def destroy()(implicit session: DBSession = MapRoute.autoSession): Unit = MapRoute.destroy(this)(session)

}


object MapRoute extends SQLSyntaxSupport[MapRoute] {

  override val tableName = "map_route"

  override val columns = Seq("id", "member_id", "area_id", "info_no", "dep", "dest", "fleet", "created")

  def apply(mr: ResultName[MapRoute])(rs: WrappedResultSet): MapRoute = new MapRoute(
    id = rs.long(mr.id),
    memberId = rs.long(mr.memberId),
    areaId = rs.int(mr.areaId),
    infoNo = rs.int(mr.infoNo),
    dep = rs.int(mr.dep),
    dest = rs.int(mr.dest),
    fleet = rs.string(mr.fleet),
    created = rs.long(mr.created)
  )

  val mr = MapRoute.syntax("mr")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[MapRoute] = {
    withSQL {
      select.from(MapRoute as mr).where.eq(mr.id, id)
    }.map(MapRoute(mr.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MapRoute] = {
    withSQL(select.from(MapRoute as mr)).map(MapRoute(mr.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MapRoute as mr)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MapRoute] = {
    withSQL {
      select.from(MapRoute as mr).where.append(sqls"${where}")
    }.map(MapRoute(mr.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MapRoute as mr).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(x: data.MapRoute, memberId: Long)(implicit session: DBSession = autoSession): MapRoute = {
    val created = System.currentTimeMillis()
    createOrig(memberId, x.areaId, x.infoNo, x.dep, x.dest, x.fleet.mkString(","), created)
  }

  def createOrig(
    memberId: Long,
    areaId: Int,
    infoNo: Int,
    dep: Int,
    dest: Int,
    fleet: String,
    created: Long)(implicit session: DBSession = autoSession): MapRoute = {
    val generatedKey = withSQL {
      insert.into(MapRoute).columns(
        column.memberId,
        column.areaId,
        column.infoNo,
        column.dep,
        column.dest,
        column.fleet,
        column.created
      ).values(
          memberId,
          areaId,
          infoNo,
          dep,
          dest,
          fleet,
          created
        )
    }.updateAndReturnGeneratedKey().apply()

    MapRoute(
      id = generatedKey,
      memberId = memberId,
      areaId = areaId,
      infoNo = infoNo,
      dep = dep,
      dest = dest,
      fleet = fleet,
      created = created)
  }

  def save(entity: MapRoute)(implicit session: DBSession = autoSession): MapRoute = {
    withSQL {
      update(MapRoute).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.areaId -> entity.areaId,
        column.infoNo -> entity.infoNo,
        column.dep -> entity.dep,
        column.dest -> entity.dest,
        column.fleet -> entity.fleet,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: MapRoute)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MapRoute).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
