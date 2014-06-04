package models

import scalikejdbc._
import scalikejdbc._
import dat.ShipWithName

/**
 *
 * @author ponkotuy
 * Date: 14/05/30.
 */
object AdmiralRanking {
  def a = Admiral.a
  lazy val m = Material.syntax("m")
  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")
  lazy val mst = MasterStype.syntax("mst")
  lazy val sb = ShipBook.syntax("sb")
  lazy val ib = ItemBook.syntax("ib")

  def findAllOrderByMaterial(limit: Int = 10, from: Long = 0)(
    implicit session: DBSession = Admiral.autoSession): List[(Admiral, Int)] = {
    withSQL {
      select.from(Admiral as a)
        .innerJoin(Material as m).on(a.id, m.memberId)
        .where.eq(m.created, sqls"(select MAX(${m.created}) from ${Material.table} as m where ${a.id} = ${m.memberId})")
        .and.gt(m.created, from)
        .orderBy(sqls"(${m.fuel} + ${m.ammo} + ${m.steel} + ${m.bauxite})").desc
        .limit(limit)
    }.map { rs =>
      val mat = Material(m)(rs)
      Admiral(a)(rs) -> (mat.fuel + mat.ammo + mat.steel + mat.bauxite)
    }.list().apply()
  }

  def findFirstShipOrderByExp(limit: Int = 10, from: Long = 0)(
      implicit session: DBSession = Ship.autoSession): List[(Admiral, ShipWithName)] = {
    withSQL {
      select.from(Ship as s)
        .innerJoin(Admiral as a).on(s.memberId, a.id)
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.eq(s.id, 1).and.gt(s.created, from)
        .orderBy(s.exp).desc
        .limit(limit)
    }.map { rs =>
      Admiral(a)(rs) -> ShipWithName(Ship(s, Nil)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
    }.list().apply()
  }

  def findAllOrderByShipBookCount(limit: Int = 10, from: Long = 0)(
      implicit session: DBSession = ShipBook.autoSession): List[(Admiral, Long)] = {
    val normal = withSQL {
      select(sb.resultAll, a.resultAll, sqls"count(1) as cnt").from(ShipBook as sb)
        .innerJoin(Admiral as a).on(sb.memberId, a.id)
        .where.gt(sb.updated, from)
        .groupBy(sb.memberId)
    }.map { rs => Admiral(a)(rs) -> rs.long("cnt") }.list().apply()
    val damaged = withSQL {
      select(sb.resultAll, a.resultAll, sqls"count(1) as cnt").from(ShipBook as sb)
        .innerJoin(Admiral as a).on(sb.memberId, a.id)
        .where.gt(sb.updated, from).and.eq(sb.isDameged, true)
        .groupBy(sb.memberId)
    }.map { rs => Admiral(a)(rs) -> rs.long("cnt") }.list().apply()
    val dCounts: Map[Long, Long] = damaged.map { case (admin, cnt) => admin.id -> cnt }.toMap.withDefaultValue(0L)
    normal.map { case (admin, cnt) =>
      admin -> (cnt + dCounts(admin.id))
    }.sortBy(_._2).reverse.take(limit)
  }

  def findAllOrderByItemBookCount(limit: Int = 10, from: Long = 0)(
      implicit session: DBSession = ItemBook.autoSession): List[(Admiral, Long)] = {
    withSQL {
      select(ib.resultAll, a.resultAll, sqls"count(1) as cnt").from(ItemBook as ib)
        .innerJoin(Admiral as a).on(ib.memberId, a.id)
        .where.gt(ib.updated, from)
        .groupBy(ib.memberId)
        .orderBy(sqls"cnt").desc
        .limit(limit)
    }.map { rs => Admiral(a)(rs) -> rs.long("cnt") }.list().apply()
  }


  def findAllOrderByShipExpSum(limit: Int = 10, from: Long = 0)(
      implicit session: DBSession = Ship.autoSession): List[(Admiral, Long)] = {
    withSQL {
      select(a.resultAll, sqls"sum(s.exp) as sum").from(Ship as s)
        .innerJoin(Admiral as a).on(s.memberId, a.id)
        .where.gt(s.created, from)
        .groupBy(s.memberId)
        .orderBy(sqls"sum").desc
        .limit(limit)
    }.map { rs =>
      Admiral(a)(rs) -> rs.long("sum")
    }.list().apply()
  }
}
