package models.db

import scalikejdbc._

case class CellPosition(
  areaId: Int,
  infoNo: Int,
  cell: Int,
  posX: Int,
  posY: Int) {

  def save()(implicit session: DBSession = CellPosition.autoSession): CellPosition = CellPosition.save(this)(session)

  def destroy()(implicit session: DBSession = CellPosition.autoSession): Unit = CellPosition.destroy(this)(session)

}


object CellPosition extends SQLSyntaxSupport[CellPosition] {

  override val tableName = "cell_position"

  override val columns = Seq("area_id", "info_no", "cell", "pos_x", "pos_y")

  def apply(cp: SyntaxProvider[CellPosition])(rs: WrappedResultSet): CellPosition = autoConstruct(rs, cp)
  def apply(cp: ResultName[CellPosition])(rs: WrappedResultSet): CellPosition = autoConstruct(rs, cp)

  val cp = CellPosition.syntax("cp")

  override val autoSession = AutoSession

  def find(areaId: Int, cell: Int, infoNo: Int)(implicit session: DBSession = autoSession): Option[CellPosition] = {
    withSQL {
      select.from(CellPosition as cp).where.eq(cp.areaId, areaId).and.eq(cp.cell, cell).and.eq(cp.infoNo, infoNo)
    }.map(CellPosition(cp.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[CellPosition] = {
    withSQL(select.from(CellPosition as cp)).map(CellPosition(cp.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(CellPosition as cp)).map(rs => rs.long(1)).single().apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[CellPosition] = {
    withSQL {
      select.from(CellPosition as cp).where.append(where)
    }.map(CellPosition(cp.resultName)).single().apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[CellPosition] = {
    withSQL {
      select.from(CellPosition as cp).where.append(where)
    }.map(CellPosition(cp.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(CellPosition as cp).where.append(where)
    }.map(_.long(1)).single().apply().get
  }

  def create(
    areaId: Int,
    infoNo: Int,
    cell: Int,
    posX: Int,
    posY: Int)(implicit session: DBSession = autoSession): CellPosition = {
    withSQL {
      insert.into(CellPosition).columns(
        column.areaId,
        column.infoNo,
        column.cell,
        column.posX,
        column.posY
      ).values(
            areaId,
            infoNo,
            cell,
            posX,
            posY
          )
    }.update().apply()

    CellPosition(
      areaId = areaId,
      infoNo = infoNo,
      cell = cell,
      posX = posX,
      posY = posY)
  }

  def save(entity: CellPosition)(implicit session: DBSession = autoSession): CellPosition = {
    withSQL {
      update(CellPosition).set(
        column.areaId -> entity.areaId,
        column.infoNo -> entity.infoNo,
        column.cell -> entity.cell,
        column.posX -> entity.posX,
        column.posY -> entity.posY
      ).where.eq(column.areaId, entity.areaId).and.eq(column.cell, entity.cell).and.eq(column.infoNo, entity.infoNo)
    }.update().apply()
    entity
  }

  def destroy(entity: CellPosition)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(CellPosition).where.eq(column.areaId, entity.areaId).and.eq(column.cell, entity.cell).and.eq(column.infoNo, entity.infoNo)
    }.update().apply()
  }

}
