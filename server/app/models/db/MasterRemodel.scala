package models.db

import models.join.MasterRemodelWithName
import scalikejdbc._
import com.ponkotuy.data

import scala.util.Try

case class MasterRemodel(
  slotitemId: Int,
  slotitemLevel: Int,
  develop: Int,
  remodel: Int,
  certainDevelop: Int,
  certainRemodel: Int,
  useSlotitemId: Int,
  useSlotitemNum: Int,
  changeFlag: Boolean) {

  def save()(implicit session: DBSession = MasterRemodel.autoSession): MasterRemodel = MasterRemodel.save(this)(session)

  def destroy()(implicit session: DBSession = MasterRemodel.autoSession): Unit = MasterRemodel.destroy(this)(session)

  def sumKit: Int = develop + remodel + certainDevelop + certainRemodel + useSlotitemNum

}


object MasterRemodel extends SQLSyntaxSupport[MasterRemodel] {

  override val tableName = "master_remodel"

  override val columns = Seq("slotitem_id", "slotitem_level", "develop", "remodel", "certain_develop", "certain_remodel", "use_slotitem_id", "use_slotitem_num", "change_flag")

  def apply(mr: SyntaxProvider[MasterRemodel])(rs: WrappedResultSet): MasterRemodel = apply(mr.resultName)(rs)
  def apply(mr: ResultName[MasterRemodel])(rs: WrappedResultSet): MasterRemodel = new MasterRemodel(
    slotitemId = rs.get(mr.slotitemId),
    slotitemLevel = rs.get(mr.slotitemLevel),
    develop = rs.get(mr.develop),
    remodel = rs.get(mr.remodel),
    certainDevelop = rs.get(mr.certainDevelop),
    certainRemodel = rs.get(mr.certainRemodel),
    useSlotitemId = rs.get(mr.useSlotitemId),
    useSlotitemNum = rs.get(mr.useSlotitemNum),
    changeFlag = rs.get(mr.changeFlag)
  )

  val mr = MasterRemodel.syntax("mr")
  val msi1 = MasterSlotItem.syntax("msi1")
  val msi2 = MasterSlotItem.syntax("msi2")

  override val autoSession = AutoSession

  def find(slotitemId: Int, slotitemLevel: Int)(implicit session: DBSession = autoSession): Option[MasterRemodel] = {
    withSQL {
      select.from(MasterRemodel as mr).where.eq(mr.slotitemId, slotitemId).and.eq(mr.slotitemLevel, slotitemLevel)
    }.map(MasterRemodel(mr.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MasterRemodel] = {
    withSQL(select.from(MasterRemodel as mr)).map(MasterRemodel(mr.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MasterRemodel as mr)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterRemodel] = {
    withSQL {
      select.from(MasterRemodel as mr).where.append(sqls"${where}").orderBy(mr.slotitemLevel)
    }.map(MasterRemodel(mr.resultName)).list().apply()
  }

  def findAllByWithName(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterRemodelWithName] = {
    withSQL {
      select.from(MasterRemodel as mr)
        .innerJoin(MasterSlotItem as msi1).on(mr.slotitemId, msi1.id)
        .leftJoin(MasterSlotItem as msi2).on(mr.useSlotitemId, msi2.id)
        .where(where).orderBy(mr.slotitemLevel)
    }.map { rs =>
      val use = Try { MasterSlotItem(msi2)(rs) }.toOption
      MasterRemodelWithName(MasterRemodel(mr)(rs), MasterSlotItem(msi1)(rs), use)
    }.list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MasterRemodel as mr).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def createFromData(x: data.master.MasterRemodel, memberId: Long)(implicit session: DBSession = autoSession): Unit = {
    SlotItem.find(x.origSlotId, memberId).map { item =>
      val orig = find(item.slotitemId, item.level)
      // 間違えてLevelが低い状態でmasterを登録した可能性があるので、元のデータを消しておく
      val isDestroy = orig.filter(_.sumKit > x.sumKit).map(_.destroy()).isDefined
      if(orig.isEmpty || isDestroy) {
        create(
          item.slotitemId,
          item.level,
          x.develop,
          x.remodel,
          x.certainDevelop,
          x.certainRemodel,
          x.slotitemId,
          x.slotitemNum,
          x.changeFlag
        )
      }
    }
  }

  def create(
    slotitemId: Int,
    slotitemLevel: Int,
    develop: Int,
    remodel: Int,
    certainDevelop: Int,
    certainRemodel: Int,
    useSlotitemId: Int,
    useSlotitemNum: Int,
    changeFlag: Boolean)(implicit session: DBSession = autoSession): MasterRemodel = {
    withSQL {
      insert.into(MasterRemodel).columns(
        column.slotitemId,
        column.slotitemLevel,
        column.develop,
        column.remodel,
        column.certainDevelop,
        column.certainRemodel,
        column.useSlotitemId,
        column.useSlotitemNum,
        column.changeFlag
      ).values(
          slotitemId,
          slotitemLevel,
          develop,
          remodel,
          certainDevelop,
          certainRemodel,
          useSlotitemId,
          useSlotitemNum,
          changeFlag
        )
    }.update().apply()

    MasterRemodel(
      slotitemId = slotitemId,
      slotitemLevel = slotitemLevel,
      develop = develop,
      remodel = remodel,
      certainDevelop = certainDevelop,
      certainRemodel = certainRemodel,
      useSlotitemId = useSlotitemId,
      useSlotitemNum = useSlotitemNum,
      changeFlag = changeFlag)
  }

  def save(entity: MasterRemodel)(implicit session: DBSession = autoSession): MasterRemodel = {
    withSQL {
      update(MasterRemodel).set(
        column.slotitemId -> entity.slotitemId,
        column.slotitemLevel -> entity.slotitemLevel,
        column.develop -> entity.develop,
        column.remodel -> entity.remodel,
        column.certainDevelop -> entity.certainDevelop,
        column.certainRemodel -> entity.certainRemodel,
        column.useSlotitemId -> entity.useSlotitemId,
        column.useSlotitemNum -> entity.useSlotitemNum,
        column.changeFlag -> entity.changeFlag
      ).where.eq(column.slotitemId, entity.slotitemId).and.eq(column.slotitemLevel, entity.slotitemLevel)
    }.update().apply()
    entity
  }

  def destroy(entity: MasterRemodel)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MasterRemodel).where.eq(column.slotitemId, entity.slotitemId).and.eq(column.slotitemLevel, entity.slotitemLevel)
    }.update().apply()
  }

}
