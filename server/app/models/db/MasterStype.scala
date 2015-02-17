package models.db

import com.ponkotuy.data.master
import scalikejdbc._
import util.scalikejdbc.BulkInsert._

case class MasterStype(
  id: Int,
  sortno: Int,
  name: String,
  scnt: Int,
  kcnt: Int) {

  def save()(implicit session: DBSession = MasterStype.autoSession): MasterStype = MasterStype.save(this)(session)

  def destroy()(implicit session: DBSession = MasterStype.autoSession): Unit = MasterStype.destroy(this)(session)

}


object MasterStype extends SQLSyntaxSupport[MasterStype] {

  override val tableName = "master_stype"

  override val columns = Seq("id", "sortno", "name", "scnt", "kcnt")

  def apply(x: SyntaxProvider[MasterStype])(rs: WrappedResultSet): MasterStype = apply(x.resultName)(rs)
  def apply(ms: ResultName[MasterStype])(rs: WrappedResultSet): MasterStype = autoConstruct(rs, ms)

  lazy val ms = MasterStype.syntax("ms")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[MasterStype] = {
    withSQL {
      select.from(MasterStype as ms).where.eq(ms.id, id)
    }.map(MasterStype(ms.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MasterStype] = {
    withSQL(select.from(MasterStype as ms)).map(MasterStype(ms.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MasterStype as ms)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterStype] = {
    withSQL {
      select.from(MasterStype as ms).where.append(sqls"${where}")
    }.map(MasterStype(ms.resultName)).list().apply()
  }

  def findAllName()(implicit session: DBSession = autoSession): List[String] = withSQL {
    select(ms.name).from(MasterStype as ms)
  }.map(_.string(1)).list().apply()

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MasterStype as ms).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    id: Int,
    sortno: Int,
    name: String,
    scnt: Int,
    kcnt: Int)(implicit session: DBSession = autoSession): MasterStype = {
    withSQL {
      insert.into(MasterStype).columns(
        column.id,
        column.sortno,
        column.name,
        column.scnt,
        column.kcnt
      ).values(
          id,
          sortno,
          name,
          scnt,
          kcnt
        )
    }.update().apply()

    MasterStype(
      id = id,
      sortno = sortno,
      name = name,
      scnt = scnt,
      kcnt = kcnt)
  }

  def bulkInsert(xs: Seq[master.MasterSType])(implicit session: DBSession = autoSession): Unit = applyUpdate {
    insert.into(MasterStype)
      .columns(column.id, column.sortno, column.name, column.scnt, column.kcnt)
      .multiValues(xs.map(_.id), xs.map(_.sortno), xs.map(_.name), xs.map(_.scnt), xs.map(_.kcnt))
  }

  def save(entity: MasterStype)(implicit session: DBSession = autoSession): MasterStype = {
    withSQL {
      update(MasterStype).set(
        column.id -> entity.id,
        column.sortno -> entity.sortno,
        column.name -> entity.name,
        column.scnt -> entity.scnt,
        column.kcnt -> entity.kcnt
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: MasterStype)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MasterStype).where.eq(column.id, entity.id)
    }.update().apply()
  }

  def deleteAll()(implicit session: DBSession = autoSession): Unit = applyUpdate {
    delete.from(MasterStype)
  }

}
