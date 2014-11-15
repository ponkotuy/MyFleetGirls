package models.db

import com.ponkotuy.data.master
import scalikejdbc._
import util.scalikejdbc.BulkInsert._

case class MasterShipAfter(
  id: Int,
  afterlv: Int,
  aftershipid: Int,
  afterfuel: Int,
  afterbull: Int) {

  def save()(implicit session: DBSession = MasterShipAfter.autoSession): MasterShipAfter = MasterShipAfter.save(this)(session)

  def destroy()(implicit session: DBSession = MasterShipAfter.autoSession): Unit = MasterShipAfter.destroy(this)(session)

}


object MasterShipAfter extends SQLSyntaxSupport[MasterShipAfter] {

  override val tableName = "master_ship_after"

  override val columns = Seq("id", "afterlv", "aftershipid", "afterfuel", "afterbull")

  def apply(msa: SyntaxProvider[MasterShipAfter])(rs: WrappedResultSet): MasterShipAfter = apply(msa.resultName)(rs)
  def apply(msa: ResultName[MasterShipAfter])(rs: WrappedResultSet): MasterShipAfter = new MasterShipAfter(
    id = rs.int(msa.id),
    afterlv = rs.int(msa.afterlv),
    aftershipid = rs.int(msa.aftershipid),
    afterfuel = rs.int(msa.afterfuel),
    afterbull = rs.int(msa.afterbull)
  )

  val msa = MasterShipAfter.syntax("msa")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[MasterShipAfter] = {
    withSQL {
      select.from(MasterShipAfter as msa).where.eq(msa.id, id)
    }.map(MasterShipAfter(msa.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MasterShipAfter] = {
    withSQL(select.from(MasterShipAfter as msa)).map(MasterShipAfter(msa.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MasterShipAfter as msa)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterShipAfter] = {
    withSQL {
      select.from(MasterShipAfter as msa).where.append(sqls"${where}")
    }.map(MasterShipAfter(msa.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MasterShipAfter as msa).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    id: Int,
    afterlv: Int,
    aftershipid: Int,
    afterfuel: Int,
    afterbull: Int)(implicit session: DBSession = autoSession): MasterShipAfter = {
    withSQL {
      insert.into(MasterShipAfter).columns(
        column.id,
        column.afterlv,
        column.aftershipid,
        column.afterfuel,
        column.afterbull
      ).values(
          id,
          afterlv,
          aftershipid,
          afterfuel,
          afterbull
        )
    }.update().apply()

    MasterShipAfter(
      id = id,
      afterlv = afterlv,
      aftershipid = aftershipid,
      afterfuel = afterfuel,
      afterbull = afterbull)
  }

  def bulkInsert(xs: Seq[master.MasterShipAfter])(implicit session: DBSession = autoSession): Unit = {
    require(xs.nonEmpty)
    applyUpdate {
      insert.into(MasterShipAfter)
        .columns(column.id, column.afterlv, column.aftershipid, column.afterfuel, column.afterbull)
        .multiValues(xs.map(_.id), xs.map(_.afterlv), xs.map(_.aftershipid), xs.map(_.afterfuel), xs.map(_.afterbull))
    }
  }

  def save(entity: MasterShipAfter)(implicit session: DBSession = autoSession): MasterShipAfter = {
    withSQL {
      update(MasterShipAfter).set(
        column.id -> entity.id,
        column.afterlv -> entity.afterlv,
        column.aftershipid -> entity.aftershipid,
        column.afterfuel -> entity.afterfuel,
        column.afterbull -> entity.afterbull
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: MasterShipAfter)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MasterShipAfter).where.eq(column.id, entity.id)
    }.update().apply()
  }

  def deleteAll()(implicit session: DBSession = autoSession): Unit = applyUpdate {
    delete.from(MasterShipAfter)
  }

}
