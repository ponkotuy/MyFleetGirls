package models.db

import com.ponkotuy.data.master
import models.join.{MasterShipWithStype, MasterShipAll}
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/02/25.
 */
case class MasterShipBase(
    id: Int, name: String, yomi: String, sortno: Int, stype: Int, filename: String
)

object MasterShipBase extends SQLSyntaxSupport[MasterShipBase] {
  override val tableName = "master_ship"
  def apply(x: SyntaxProvider[MasterShipBase])(rs: WrappedResultSet): MasterShipBase = apply(x.resultName)(rs)
  def apply(x: ResultName[MasterShipBase])(rs: WrappedResultSet): MasterShipBase = autoConstruct(rs, x)

  lazy val ms = MasterShipBase.syntax("ms")
  lazy val mss = MasterShipSpecs.syntax("mss")
  lazy val mst = MasterStype.syntax("mst")
  lazy val msa = MasterShipAfter.syntax("msa")
  lazy val mso = MasterShipOther.syntax("mso")
  lazy val msb = MasterShipBase.syntax("msb") // 2つのMasterShipBaseを区別する必要がある時用

  def findAllInOneBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterShipAll] = withSQL {
    select.from(MasterShipBase as ms)
      .innerJoin(MasterShipSpecs as mss).on(ms.id, mss.id)
      .innerJoin(MasterShipAfter as msa).on(ms.id, msa.id)
      .innerJoin(MasterShipOther as mso).on(ms.id, mso.id)
      .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
      .where(sqls"$where")
  }.map { rs =>
    val _msb = MasterShipBase(ms)(rs)
    val _mss = MasterShipSpecs(mss)(rs)
    val _msa = MasterShipAfter(msa)(rs)
    val _mso = MasterShipOther(mso)(rs)
    val _mst = MasterStype(mst)(rs)
    MasterShipAll(_msb, _mss, _msa, _mso, _mst)
  }.list().apply()

  def findByFilename(key: String)(implicit session: DBSession = autoSession): Option[MasterShipBase] = withSQL {
    select.from(MasterShipBase as ms).where.eq(ms.filename, key)
  } .map(MasterShipBase(ms)).single().apply()

  def findAll()(implicit session: DBSession = MasterShipBase.autoSession): List[MasterShipBase] = withSQL {
    select.from(MasterShipBase as ms)
  }.map(MasterShipBase(ms)).toList().apply()

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterShipBase] = withSQL {
    select.from(MasterShipBase as ms).where(where)
  }.map(MasterShipBase(ms)).toList().apply()

  def findAllByLike(q: String)(implicit session: DBSession = autoSession): List[MasterShipBase] = withSQL {
    select.from(MasterShipBase as ms).where.like(ms.name, q)
  }.map(MasterShipBase(ms)).toList().apply()

  def findInWithStype(shipIds: List[Int])(implicit session: DBSession = autoSession): List[MasterShipWithStype] =
    withSQL {
      select.from(MasterShipBase as ms)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.in(ms.id, shipIds)
    }.map(MasterShipWithStype(ms, mst)).list().apply()

  def findAllWithStype(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterShipWithStype] = {
    withSQL {
      select.from(MasterShipBase as ms)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id).where(sqls"$where")
        .orderBy(ms.sortno)
    }.map(MasterShipWithStype(ms, mst)).list().apply()
  }

  def count()(implicit session: DBSession = MasterShipBase.autoSession): Long = withSQL {
    select(sqls"count(1)").from(MasterShipBase as ms)
  }.map(rs => rs.long(1)).single().apply().get

  def create(ms: master.MasterShipBase)(implicit session: DBSession = MasterShipBase.autoSession): MasterShipBase = {
    withSQL {
      insert.into(MasterShipBase).namedValues(
        column.id -> ms.id, column.name -> ms.name, column.yomi -> ms.yomi,
        column.sortno -> ms.sortno, column.stype -> ms.stype,
        column.filename -> ms.filename
      )
    }.update().apply()
    MasterShipBase(ms.id, ms.name, ms.yomi, ms.sortno, ms.stype, ms.filename)
  }

  def deleteAll()(implicit session: DBSession = MasterShipBase.autoSession): Unit =
    withSQL { delete.from(MasterShipBase) }.update().apply()
}
