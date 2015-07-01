package models.db

import scalikejdbc._

case class MapImage(
  areaId: Int,
  infoNo: Int,
  image: Array[Byte],
  version: Short) {

  def save()(implicit session: DBSession = MapImage.autoSession): MapImage = MapImage.save(this)(session)

  def destroy()(implicit session: DBSession = MapImage.autoSession): Unit = MapImage.destroy(this)(session)

}


object MapImage extends SQLSyntaxSupport[MapImage] {

  override val tableName = "map_image"

  override val columns = Seq("area_id", "info_no", "image", "version")

  def apply(mi: SyntaxProvider[MapImage])(rs: WrappedResultSet): MapImage = autoConstruct(rs, mi)
  def apply(mi: ResultName[MapImage])(rs: WrappedResultSet): MapImage = autoConstruct(rs, mi)

  val mi = MapImage.syntax("mi")

  override val autoSession = AutoSession

  def find(areaId: Int, infoNo: Int, version: Short)(implicit session: DBSession = autoSession): Option[MapImage] = {
    withSQL {
      select.from(MapImage as mi).where.eq(mi.areaId, areaId).and.eq(mi.infoNo, infoNo).and.eq(mi.version, version)
    }.map(MapImage(mi.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MapImage] = {
    withSQL(select.from(MapImage as mi)).map(MapImage(mi.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(MapImage as mi)).map(rs => rs.long(1)).single().apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[MapImage] = {
    withSQL {
      select.from(MapImage as mi).where.append(where)
    }.map(MapImage(mi.resultName)).single().apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MapImage] = {
    withSQL {
      select.from(MapImage as mi).where.append(where)
    }.map(MapImage(mi.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(MapImage as mi).where.append(where)
    }.map(_.long(1)).single().apply().get
  }

  def create(
    areaId: Int,
    infoNo: Int,
    image: Array[Byte],
    version: Short)(implicit session: DBSession = autoSession): MapImage = {
    withSQL {
      insert.into(MapImage).columns(
        column.areaId,
        column.infoNo,
        column.image,
        column.version
      ).values(
            areaId,
            infoNo,
            image,
            version
          )
    }.update().apply()

    MapImage(
      areaId = areaId,
      infoNo = infoNo,
      image = image,
      version = version)
  }

  def save(entity: MapImage)(implicit session: DBSession = autoSession): MapImage = {
    withSQL {
      update(MapImage).set(
        column.areaId -> entity.areaId,
        column.infoNo -> entity.infoNo,
        column.image -> entity.image,
        column.version -> entity.version
      ).where.eq(column.areaId, entity.areaId).and.eq(column.infoNo, entity.infoNo).and.eq(column.version, entity.version)
    }.update().apply()
    entity
  }

  def destroy(entity: MapImage)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MapImage).where.eq(column.areaId, entity.areaId).and.eq(column.infoNo, entity.infoNo).and.eq(column.version, entity.version)
    }.update().apply()
  }

}
