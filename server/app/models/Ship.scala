package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{WrappedResultSet, DBSession}
import com.ponkotuy.data
import util.scalikejdbc.BulkInsert._
import dat.ShipWithName

/**
 *
 * @author ponkotuy
 * Date: 14/02/26.
 */
case class Ship(
    id: Int, shipId: Int, memberId: Long,
    lv: Int, exp: Int, nowhp: Int, slot: List[Int], fuel: Int, bull: Int, dockTime: Long, cond: Int,
    karyoku: Int, raisou: Int, taiku: Int, soukou: Int, kaihi: Int, taisen: Int, sakuteki: Int, lucky: Int, locked: Boolean,
    created: Long)

object Ship extends SQLSyntaxSupport[Ship] {
  override val columnNames = Seq("id", "ship_id", "member_id", "lv", "exp", "nowhp", "fuel", "bull", "dock_time", "cond",
    "karyoku", "raisou", "taiku", "soukou", "kaihi", "taisen", "sakuteki", "lucky", "locked", "created")
  def apply(x: SyntaxProvider[Ship])(rs: WrappedResultSet): Ship =
    apply(x.resultName)(rs)
  def apply(x: ResultName[Ship])(rs: WrappedResultSet): Ship = {
    val id = rs.int(x.id)
    val memberId = rs.long(x.memberId)
    val slot = findSlot(memberId, id)
    new Ship(
      id,
      rs.int(x.shipId),
      memberId,
      rs.int(x.lv),
      rs.int(x.exp),
      rs.int(x.nowhp),
      slot,
      rs.int(x.fuel),
      rs.int(x.bull),
      rs.long(x.dockTime),
      rs.int(x.cond),
      rs.int(x.karyoku),
      rs.int(x.raisou),
      rs.int(x.taiku),
      rs.int(x.soukou),
      rs.int(x.kaihi),
      rs.int(x.taisen),
      rs.int(x.sakuteki),
      rs.int(x.lucky),
      rs.boolean(x.locked),
      rs.long(x.created)
    )
  }

  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")
  lazy val ds = DeckShip.syntax("ds")
  lazy val ssi = ShipSlotItem.syntax("ssi")
  lazy val mst = MasterStype.syntax("mst")

  def findByUserMaxLvWithName(memberId: Long)(implicit session: DBSession = autoSession): Option[ShipWithName] = {
    withSQL {
      select.from(Ship as s)
        .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.eq(s.memberId, memberId)
        .and.eq(s.lv, sqls"(SELECT MAX(${s.lv}) FROM ${Ship.table} s WHERE ${s.memberId} = ${memberId})")
    }.map { rs =>
      ShipWithName(Ship(s)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
    }.first().apply()
  }

  def findByIDWithName(memberId: Long, sid: Int)(
      implicit session: DBSession = autoSession): Option[ShipWithName] = {
    withSQL {
      select.from(Ship as s)
        .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.eq(s.memberId, memberId).and.eq(s.id, sid)
    }.map { rs =>
      ShipWithName(Ship(s)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
    }.first().apply()
  }

  def findIn(memberId: Long, ids: Seq[Int])(implicit session: DBSession = autoSession): List[ShipWithName] =
    ids match {
      case Seq() => Nil
      case _ =>
        withSQL {
          select.from(Ship as s)
            .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
            .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
            .where.eq(s.memberId, memberId).and.in(s.id, ids)
        }.map { rs =>
          ShipWithName(Ship(s)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
        }.toList().apply()
    }

  def findAllByUser(memberId: Long)(implicit session: DBSession = Ship.autoSession): List[Ship] = withSQL {
    select.from(Ship as s)
      .where.eq(s.memberId, memberId)
  }.map(Ship(s)).toList().apply()

  def findAllByUserWithName(memberId: Long)(implicit session: DBSession = Ship.autoSession): List[ShipWithName] = {
    withSQL {
      select.from(Ship as s)
        .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.eq(s.memberId, memberId)
    }.map { rs =>
      ShipWithName(Ship(s)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
    }.toList().apply()
  }

  def findSlot(memberId: Long, shipId: Int): List[Int] =
    ShipSlotItem.findAllBy(sqls"member_id = ${memberId} and ship_id = ${shipId}").map(_.slotitemId)

  def create(s: data.Ship, memberId: Long)(implicit session: DBSession = Ship.autoSession): Ship = {
    val created = System.currentTimeMillis()
    ShipSlotItem.bulkInsert(s.slot, memberId, s.id)
    withSQL {
      insert.into(Ship).namedValues(
        column.id -> s.id, column.shipId -> s.shipId, column.memberId -> memberId,
        column.lv -> s.lv, column.exp -> s.exp, column.nowhp -> s.nowhp,
        column.fuel -> s.fuel, column.bull -> s.bull, column.dockTime -> s.dockTime, column.cond -> s.cond,
        column.karyoku -> s.karyoku, column.raisou -> s.raisou, column.taiku -> s.taiku, column.soukou -> s.soukou,
        column.kaihi -> s.kaihi, column.taisen -> s.taisen, column.sakuteki -> s.sakuteki, column.lucky -> s.lucky,
        column.locked -> s.locked, column.created -> created
      )
    }.update().apply()
    Ship(s.id, s.shipId, memberId, s.lv, s.exp, s.nowhp, s.slot, s.fuel, s.bull, s.dockTime, s.cond,
      s.karyoku, s.raisou, s.taiku, s.soukou, s.kaihi, s.taisen, s.sakuteki, s.lucky, s.locked, created
    )
  }

  def bulkInsert(ss: Seq[data.Ship], memberId: Long)(
      implicit session: DBSession = Ship.autoSession): Seq[Ship] = {
    val created = System.currentTimeMillis()
    ShipSlotItem.bulkInserts(ss.map(_.slot), memberId, ss.map(_.id))
    applyUpdate {
      insert.into(Ship).columns(
        column.id, column.shipId, column.memberId,
        column.lv, column.exp, column.nowhp,
        column.fuel, column.bull, column.dockTime, column.cond,
        column.karyoku, column.raisou, column.taiku, column.soukou,
        column.kaihi, column.taisen, column.sakuteki, column.lucky,
        column.locked, column.created
      ).multiValues(
          ss.map(_.id), ss.map(_.shipId), Seq.fill(ss.size)(memberId),
          ss.map(_.lv), ss.map(_.exp), ss.map(_.nowhp),
          ss.map(_.fuel), ss.map(_.bull), ss.map(_.dockTime), ss.map(_.cond),
          ss.map(_.karyoku), ss.map(_.raisou), ss.map(_.taiku), ss.map(_.soukou),
          ss.map(_.kaihi), ss.map(_.taisen), ss.map(_.sakuteki), ss.map(_.lucky),
          ss.map(_.locked), Seq.fill(ss.size)(created)
        )
    }
    ss.map { s =>
      Ship(
        s.id, s.shipId, memberId, s.lv, s.exp, s.nowhp, s.slot,
        s.fuel, s.bull, s.dockTime, s.cond, s.karyoku, s.raisou, s.taiku, s.soukou,
        s.kaihi, s.taisen, s.sakuteki, s.lucky, s.locked, created
      )
    }
  }

  def destroy(memberId: Long, id: Int)(implicit session: DBSession = autoSession): Unit = {
    ShipSlotItem.deleteAllBy(sqls"member_id = ${memberId} and ship_id = ${s.id}")
    applyUpdate {
      delete.from(Ship)
        .where.eq(Ship.column.memberId, memberId).and.eq(Ship.column.id, id)
    }
  }

  def deleteAllByUser(memberId: Long)(implicit session: DBSession = Ship.autoSession): Unit = {
    ShipSlotItem.deleteAllBy(sqls"member_id = ${memberId}")
    applyUpdate { delete.from(Ship).where.eq(Ship.column.memberId, memberId) }
  }
}


