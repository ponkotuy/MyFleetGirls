package models.db

import scalikejdbc._
import com.ponkotuy.data

case class Remodel(
  id: Long,
  memberId: Long,
  flag: Boolean,
  beforeItemId: Int,
  afterItemId: Int,
  vocieId: Int,
  useSlotIds: String,
  certain: Boolean) {

  def save()(implicit session: DBSession = Remodel.autoSession): Remodel = Remodel.save(this)(session)

  def destroy()(implicit session: DBSession = Remodel.autoSession): Unit = Remodel.destroy(this)(session)

}


object Remodel extends SQLSyntaxSupport[Remodel] {

  override val tableName = "remodel"

  override val columns = Seq("id", "member_id", "flag", "before_item_id", "after_item_id", "vocie_id", "use_slot_ids", "certain")

  def apply(r: SyntaxProvider[Remodel])(rs: WrappedResultSet): Remodel = apply(r.resultName)(rs)
  def apply(r: ResultName[Remodel])(rs: WrappedResultSet): Remodel = autoConstruct(rs, r)

  val r = Remodel.syntax("r")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Remodel] = {
    withSQL {
      select.from(Remodel as r).where.eq(r.id, id)
    }.map(Remodel(r.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Remodel] = {
    withSQL(select.from(Remodel as r)).map(Remodel(r.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Remodel as r)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Remodel] = {
    withSQL {
      select.from(Remodel as r).where.append(sqls"${where}")
    }.map(Remodel(r.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Remodel as r).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(x: data.Remodel, memberId: Long)(implicit session: DBSession = autoSession): Remodel = {
    val record =
      createOrig(memberId, x.flag, x.beforeItemId, x.afterItemId, x.voiceId, x.useSlotIds.mkString(","), x.certain)
    x.afterSlot.foreach { y =>
      RemodelAfterSlot.create(record.id, y.id, y.slotitemId, y.locked, y.level)
    }
    record
  }

  def createOrig(
    memberId: Long,
    flag: Boolean,
    beforeItemId: Int,
    afterItemId: Int,
    vocieId: Int,
    useSlotIds: String,
    certain: Boolean)(implicit session: DBSession = autoSession): Remodel = {
    val generatedKey = withSQL {
      insert.into(Remodel).columns(
        column.memberId,
        column.flag,
        column.beforeItemId,
        column.afterItemId,
        column.vocieId,
        column.useSlotIds,
        column.certain
      ).values(
          memberId,
          flag,
          beforeItemId,
          afterItemId,
          vocieId,
          useSlotIds,
          certain
        )
    }.updateAndReturnGeneratedKey().apply()

    Remodel(
      id = generatedKey,
      memberId = memberId,
      flag = flag,
      beforeItemId = beforeItemId,
      afterItemId = afterItemId,
      vocieId = vocieId,
      useSlotIds = useSlotIds,
      certain = certain)
  }

  def save(entity: Remodel)(implicit session: DBSession = autoSession): Remodel = {
    withSQL {
      update(Remodel).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.flag -> entity.flag,
        column.beforeItemId -> entity.beforeItemId,
        column.afterItemId -> entity.afterItemId,
        column.vocieId -> entity.vocieId,
        column.useSlotIds -> entity.useSlotIds,
        column.certain -> entity.certain
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: Remodel)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(Remodel).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
