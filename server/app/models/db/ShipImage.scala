package models.db

import scalikejdbc._

case class ShipImage(
    id: Int,
    image: Array[Byte],
    filename: String,
    memberId: Long,
    swfId: Int,
    version: Int) {

  def save()(implicit session: DBSession = ShipImage.autoSession): ShipImage = ShipImage.save(this)(session)

  def destroy()(implicit session: DBSession = ShipImage.autoSession): Unit = ShipImage.destroy(this)(session)

}

object ShipImage extends SQLSyntaxSupport[ShipImage] {

  override val tableName = "ship_image"

  override val columns = Seq("id", "image", "filename", "member_id", "swf_id", "version")

  def apply(si: ResultName[ShipImage])(rs: WrappedResultSet): ShipImage = autoConstruct(rs, si)

  lazy val si = ShipImage.syntax("si")
  lazy val a = Admiral.syntax("a")

  override val autoSession = AutoSession

  def find(id: Int, swfId: Int)(implicit session: DBSession = autoSession): Option[ShipImage] = {
    withSQL {
      select.from(ShipImage as si)
          .where.eq(si.id, id).and.eq(si.swfId, swfId)
          .orderBy(si.version.desc).limit(1)
    }.map(ShipImage(si.resultName)).single().apply()
  }

  def findAdmiral(sid: Int)(implicit session: DBSession = autoSession): Option[Admiral] = withSQL {
    select(a.resultAll).from(ShipImage as si)
        .innerJoin(Admiral as a).on(si.memberId, a.id)
        .where.eq(si.id, sid)
        .orderBy(si.swfId.asc, si.version.desc).limit(1)
  }.map(Admiral(a)).single().apply()

  def findAll()(implicit session: DBSession = autoSession): List[ShipImage] = {
    withSQL(select.from(ShipImage as si)).map(ShipImage(si.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(ShipImage as si)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ShipImage] = {
    withSQL {
      select.from(ShipImage as si).where.append(sqls"${where}")
    }.map(ShipImage(si.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(ShipImage as si).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
      id: Int,
      image: Array[Byte],
      filename: String,
      memberId: Long,
      swfId: Int,
      version: Int)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      insert.into(ShipImage).columns(
        column.id,
        column.image,
        column.filename,
        column.memberId,
        column.swfId,
        column.version
      ).values(
          id,
          image,
          filename,
          memberId,
          swfId,
          version
        )
    }.update().apply()
  }

  def save(entity: ShipImage)(implicit session: DBSession = autoSession): ShipImage = {
    withSQL {
      update(ShipImage).set(
        column.image -> entity.image,
        column.filename -> entity.filename,
        column.memberId -> entity.memberId
      ).where.eq(column.id, entity.id).and.eq(column.swfId, entity.swfId).and.eq(column.version, entity.version)
    }.update().apply()
    entity
  }

  def destroy(entity: ShipImage)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(ShipImage)
          .where.eq(column.id, entity.id).and.eq(column.swfId, entity.swfId).and.eq(column.version, entity.version)
    }.update().apply()
  }

}
