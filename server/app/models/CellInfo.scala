package models

import scalikejdbc._
import scalikejdbc._

case class CellInfo(
  areaId: Int,
  infoNo: Int,
  cell: Int,
  alphabet: String,
  start: Boolean,
  boss: Boolean) {

  def save()(implicit session: DBSession = CellInfo.autoSession): CellInfo = CellInfo.save(this)(session)

  def destroy()(implicit session: DBSession = CellInfo.autoSession): Unit = CellInfo.destroy(this)(session)

  def pointStr: String = s"$areaId-$infoNo-$cell"
  def pointAlpha: String = s"$areaId-$infoNo-$alphabet"

}


object CellInfo extends SQLSyntaxSupport[CellInfo] {

  override val tableName = "cell_info"

  override val columns = Seq("area_id", "info_no", "cell", "alphabet", "start", "boss")

  def apply(ci: SyntaxProvider[CellInfo])(rs: WrappedResultSet): CellInfo = apply(ci.resultName)(rs)
  def apply(ci: ResultName[CellInfo])(rs: WrappedResultSet): CellInfo = new CellInfo(
    areaId = rs.get(ci.areaId),
    infoNo = rs.get(ci.infoNo),
    cell = rs.get(ci.cell),
    alphabet = rs.get(ci.alphabet),
    start = rs.get(ci.start),
    boss = rs.get(ci.boss)
  )

  val ci = CellInfo.syntax("ci")

  override val autoSession = AutoSession

  /** Heap Cache */
  lazy val all: List[CellInfo] = {
    implicit val session = autoSession
    withSQL(select.from(CellInfo as ci)).map(CellInfo(ci.resultName)).list().apply()
  }

  /** from Heap */
  def find(areaId: Int, infoNo: Int, cell: Int): Option[CellInfo] =
    all.find(c => c.areaId == areaId && c.infoNo == infoNo && c.cell == cell)

  def findOrDefault(areaId: Int, infoNo: Int, cell: Int): CellInfo =
    find(areaId, infoNo, cell).getOrElse(noAlphabet(areaId, infoNo, cell))

  /** from Heap */
  def findAll(): List[CellInfo] = all

  /** from Heap */
  def countAll(): Long = all.size

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[CellInfo] = {
    withSQL {
      select.from(CellInfo as ci).where.append(sqls"${where}")
    }.map(CellInfo(ci.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(CellInfo as ci).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    areaId: Int,
    infoNo: Int,
    cell: Int,
    alphabet: String,
    start: Boolean,
    boss: Boolean)(implicit session: DBSession = autoSession): CellInfo = {
    withSQL {
      insert.into(CellInfo).columns(
        column.areaId,
        column.infoNo,
        column.cell,
        column.alphabet,
        column.start,
        column.boss
      ).values(
          areaId,
          infoNo,
          cell,
          alphabet,
          start,
          boss
        )
    }.update().apply()

    CellInfo(
      areaId = areaId,
      infoNo = infoNo,
      cell = cell,
      alphabet = alphabet,
      start = start,
      boss = boss)
  }

  def save(entity: CellInfo)(implicit session: DBSession = autoSession): CellInfo = {
    withSQL {
      update(CellInfo).set(
        column.areaId -> entity.areaId,
        column.infoNo -> entity.infoNo,
        column.cell -> entity.cell,
        column.alphabet -> entity.alphabet,
        column.start -> entity.start,
        column.boss -> entity.boss
      ).where.eq(column.areaId, entity.areaId).and.eq(column.cell, entity.cell).and.eq(column.infoNo, entity.infoNo)
    }.update().apply()
    entity
  }

  def destroy(entity: CellInfo)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(CellInfo).where.eq(column.areaId, entity.areaId).and.eq(column.cell, entity.cell).and.eq(column.infoNo, entity.infoNo)
    }.update().apply()
  }

  def noAlphabet(area: Int, info: Int, cell: Int): CellInfo = CellInfo(area, info, cell, "", false, false)

}
