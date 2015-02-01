package models.db

import scalikejdbc._

case class RemodelAfterSlot(
  remodelId: Long,
  id: Int,
  slotitemId: Int,
  locked: Boolean,
  level: Int) {

  def save()(implicit session: DBSession = RemodelAfterSlot.autoSession): RemodelAfterSlot = RemodelAfterSlot.save(this)(session)

  def destroy()(implicit session: DBSession = RemodelAfterSlot.autoSession): Unit = RemodelAfterSlot.destroy(this)(session)

}


object RemodelAfterSlot extends SQLSyntaxSupport[RemodelAfterSlot] {

  override val tableName = "remodel_after_slot"

  override val columns = Seq("remodel_id", "id", "slotitem_id", "locked", "level")

  def apply(ras: SyntaxProvider[RemodelAfterSlot])(rs: WrappedResultSet): RemodelAfterSlot = apply(ras.resultName)(rs)
  def apply(ras: ResultName[RemodelAfterSlot])(rs: WrappedResultSet): RemodelAfterSlot = autoConstruct(rs, ras)

  val ras = RemodelAfterSlot.syntax("ras")

  override val autoSession = AutoSession

  def find(remodelId: Long)(implicit session: DBSession = autoSession): Option[RemodelAfterSlot] = {
    withSQL {
      select.from(RemodelAfterSlot as ras).where.eq(ras.remodelId, remodelId)
    }.map(RemodelAfterSlot(ras.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[RemodelAfterSlot] = {
    withSQL(select.from(RemodelAfterSlot as ras)).map(RemodelAfterSlot(ras.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(RemodelAfterSlot as ras)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[RemodelAfterSlot] = {
    withSQL {
      select.from(RemodelAfterSlot as ras).where.append(sqls"${where}")
    }.map(RemodelAfterSlot(ras.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(RemodelAfterSlot as ras).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    remodelId: Long,
    id: Int,
    slotitemId: Int,
    locked: Boolean,
    level: Int)(implicit session: DBSession = autoSession): RemodelAfterSlot = {
    withSQL {
      insert.into(RemodelAfterSlot).columns(
        column.remodelId,
        column.id,
        column.slotitemId,
        column.locked,
        column.level
      ).values(
          remodelId,
          id,
          slotitemId,
          locked,
          level
        )
    }.update().apply()

    RemodelAfterSlot(
      remodelId = remodelId,
      id = id,
      slotitemId = slotitemId,
      locked = locked,
      level = level)
  }

  def save(entity: RemodelAfterSlot)(implicit session: DBSession = autoSession): RemodelAfterSlot = {
    withSQL {
      update(RemodelAfterSlot).set(
        column.remodelId -> entity.remodelId,
        column.id -> entity.id,
        column.slotitemId -> entity.slotitemId,
        column.locked -> entity.locked,
        column.level -> entity.level
      ).where.eq(column.remodelId, entity.remodelId)
    }.update().apply()
    entity
  }

  def destroy(entity: RemodelAfterSlot)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(RemodelAfterSlot).where.eq(column.remodelId, entity.remodelId)
    }.update().apply()
  }

}
