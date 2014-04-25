package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{WrappedResultSet, DBSession}
import com.ponkotuy.data.master
import dat.{MasterShipWithStype, MasterShipWithClass}

/**
 *
 * @author ponkotuy
 * Date: 14/02/25.
 */
case class MasterShipBase(
    id: Int, name: String, yomi: String, sortno: Int, stype: Int, ctype: Int, cnum: Int, filename: String
)

object MasterShipBase extends SQLSyntaxSupport[MasterShipBase] {
  override val tableName = "master_ship"
  def apply(x: SyntaxProvider[MasterShipBase])(rs: WrappedResultSet): MasterShipBase = apply(x.resultName)(rs)
  def apply(x: ResultName[MasterShipBase])(rs: WrappedResultSet): MasterShipBase = new MasterShipBase(
    rs.int(x.id),
    rs.string(x.name),
    rs.string(x.yomi),
    rs.int(x.sortno),
    rs.int(x.stype),
    rs.int(x.ctype),
    rs.int(x.cnum),
    rs.string(x.filename)
  )

  lazy val ms = MasterShipBase.syntax("ms")
  lazy val mst = MasterStype.syntax("mst")
  lazy val msb = MasterShipBase.syntax("msb") // 2つのMasterShipBaseを区別する必要がある時用

  def findByFilename(key: String)(implicit session: DBSession = autoSession): Option[MasterShipBase] = withSQL {
    select.from(MasterShipBase as ms).where.eq(ms.filename, key)
  } .map(MasterShipBase(ms)).single().apply()

  def findAll()(implicit session: DBSession = MasterShipBase.autoSession): List[MasterShipBase] = withSQL {
    select.from(MasterShipBase as ms)
  }.map(MasterShipBase(ms)).toList().apply()

  def findAllByLike(q: String)(implicit session: DBSession = autoSession): List[MasterShipBase] = withSQL {
    select.from(MasterShipBase as ms).where.like(ms.name, q)
  }.map(MasterShipBase(ms)).toList().apply()

  def findAllWithClass()(implicit session: DBSession = autoSession): List[MasterShipWithClass] = withSQL {
    select.from(MasterShipBase as ms)
      .leftJoin(MasterShipBase as msb).on(sqls"ms.ctype = msb.ctype and msb.cnum = 1 and msb.sortno != 0")
  }.map(MasterShipWithClass(ms, msb))
    .list().apply()
    .groupBy(_.shipId).map { case (_, ships) => ships.head } // 改などを外す
    .toList

  def findInWithClass(shipIds: Seq[Int])(implicit session: DBSession = autoSession): List[MasterShipWithClass] =
    withSQL {
      select.from(MasterShipBase as ms)
        .leftJoin(MasterShipBase as msb).on(sqls"ms.ctype = msb.ctype and msb.cnum = 1 and msb.sortno != 0")
        .where.in(ms.id, shipIds)
    }.map(MasterShipWithClass(ms, msb))
      .list().apply()
      .groupBy(_.shipId).map { case (_, ships) => ships.head } // 改などを外す
      .toList

  def findInWithStype(shipIds: List[Int])(implicit session: DBSession = autoSession): List[MasterShipWithStype] =
    withSQL {
      select.from(MasterShipBase as ms)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.in(ms.id, shipIds)
    }.map(MasterShipWithStype(ms, mst)).list().apply()

  def count()(implicit session: DBSession = MasterShipBase.autoSession): Long = withSQL {
    select(sqls"count(1)").from(MasterShipBase as ms)
  }.map(rs => rs.long(1)).single().apply().get

  def create(ms: master.MasterShipBase)(implicit session: DBSession = MasterShipBase.autoSession): MasterShipBase = {
    withSQL {
      insert.into(MasterShipBase).namedValues(
        column.id -> ms.id, column.name -> ms.name, column.yomi -> ms.yomi,
        column.sortno -> ms.sortno, column.stype -> ms.stype, column.ctype -> ms.ctype, column.cnum -> ms.cnum,
        column.filename -> ms.filename
      )
    }.update().apply()
    MasterShipBase(ms.id, ms.name, ms.yomi, ms.sortno, ms.stype, ms.ctype, ms.cnum, ms.filename)
  }

  def deleteAll()(implicit session: DBSession = MasterShipBase.autoSession): Unit =
    withSQL { delete.from(MasterShipBase) }.update().apply()
}
