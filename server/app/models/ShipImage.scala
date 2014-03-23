package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._

case class ShipImage(
  id: Int,
  image: Array[Byte]) {

  def save()(implicit session: DBSession = ShipImage.autoSession): ShipImage = ShipImage.save(this)(session)

  def destroy()(implicit session: DBSession = ShipImage.autoSession): Unit = ShipImage.destroy(this)(session)

}

object ShipImage extends SQLSyntaxSupport[ShipImage] {

  override val tableName = "ship_image"

  override val columns = Seq("id", "image")

  def apply(si: ResultName[ShipImage])(rs: WrappedResultSet): ShipImage = new ShipImage(
    id = rs.int(si.id),
    image = rs.bytes(si.image)
  )

  val si = ShipImage.syntax("si")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[ShipImage] = {
    withSQL {
      select.from(ShipImage as si).where.eq(si.id, id)
    }.map(ShipImage(si.resultName)).single().apply()
  }

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
    image: Array[Byte])(implicit session: DBSession = autoSession): ShipImage = {
    withSQL {
      insert.into(ShipImage).columns(
        column.id,
        column.image
      ).values(
          id,
          image
        )
    }.update().apply()

    ShipImage(
      id = id,
      image = image)
  }

  def save(entity: ShipImage)(implicit session: DBSession = autoSession): ShipImage = {
    withSQL {
      update(ShipImage).set(
        column.id -> entity.id,
        column.image -> entity.image
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: ShipImage)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(ShipImage).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
