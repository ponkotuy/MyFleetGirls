package models

import scalikejdbc._
import com.ponkotuy.data
import util.scalikejdbc.BulkInsert._
import dat.{ShipWithAdmiral, ShipWithName}

/**
 *
 * @author ponkotuy
 * Date: 14/02/26.
 */
case class Ship(
    id: Int, shipId: Int, memberId: Long,
    lv: Int, exp: Int, nowhp: Int, slot: List[Int], fuel: Int, bull: Int, dockTime: Long, cond: Int,
    karyoku: Int, raisou: Int, taiku: Int, soukou: Int, kaihi: Int, taisen: Int, sakuteki: Int, lucky: Int, locked: Boolean,
    created: Long, maxhp: Int)

object Ship extends SQLSyntaxSupport[Ship] {
  override val columnNames = Seq("id", "ship_id", "member_id", "lv", "exp", "nowhp", "fuel", "bull", "dock_time", "cond",
    "karyoku", "raisou", "taiku", "soukou", "kaihi", "taisen", "sakuteki", "lucky", "locked", "created", "maxhp")
  def apply(x: SyntaxProvider[Ship], slot: List[Int])(rs: WrappedResultSet): Ship =
    apply(x.resultName, slot)(rs)
  def apply(x: ResultName[Ship], slot: List[Int])(rs: WrappedResultSet): Ship = {
    new Ship(
      rs.int(x.id),
      rs.int(x.shipId),
      rs.long(x.memberId),
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
      rs.long(x.created),
      rs.int(x.maxhp)
    )
  }

  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")
  lazy val ds = DeckShip.syntax("ds")
  lazy val ssi = ShipSlotItem.syntax("ssi")
  lazy val mst = MasterStype.syntax("mst")
  lazy val a = Admiral.syntax("a")

  def findByUserMaxLvWithName(memberId: Long)(implicit session: DBSession = autoSession): Option[ShipWithName] = {
    withSQL {
      select.from(Ship as s)
        .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.eq(s.memberId, memberId)
        .and.eq(s.lv, sqls"(SELECT MAX(${s.lv}) FROM ${Ship.table} s WHERE ${s.memberId} = ${memberId})")
    }.map { rs =>
      val slot = findSlot(memberId, rs.int(s.resultName.id))
      ShipWithName(Ship(s, slot)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
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
      val slot = findSlot(memberId, rs.int(s.resultName.id))
      ShipWithName(Ship(s, slot)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
    }.first().apply()
  }

  // Slotは常にNil
  def findByWithAdmiral(sid: Int, limit: Int = 50)(implicit session: DBSession = autoSession): List[ShipWithAdmiral] = withSQL {
    select.from(Ship as s)
      .innerJoin(Admiral as a).on(s.memberId, a.id)
      .where.eq(s.shipId, sid).and.gt(s.lv, 10).orderBy(s.exp).desc
      .limit(limit)
  }.map { rs =>
    ShipWithAdmiral(Ship(s, Nil)(rs), Admiral(a)(rs))
  }.toList().apply()

  def findIn(memberId: Long, ids: Seq[Int])(implicit session: DBSession = autoSession): List[ShipWithName] =
    ids match {
      case Seq() => Nil
      case _ =>
        val slots = ShipSlotItem.findAllInShip(memberId, ids)
        withSQL {
          select.from(Ship as s)
            .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
            .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
            .where.eq(s.memberId, memberId).and.in(s.id, ids)
        }.map { rs =>
          val id = rs.int(s.resultName.id)
          val slot = slots.filter(_.shipId == id).map(_.slotitemId)
          ShipWithName(Ship(s, slot)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
        }.toList().apply()
    }

  def findAllByUser(memberId: Long)(implicit session: DBSession = Ship.autoSession): List[Ship] = {
    val slots = ShipSlotItem.findAllBy(sqls"member_id = ${memberId}")
    withSQL {
      select.from(Ship as s)
        .where.eq(s.memberId, memberId)
    }.map { rs =>
      val id = rs.int(s.resultName.id)
      val slot = slots.filter(_.shipId == id).map(_.slotitemId)
      Ship(s, slot)(rs)
    }.toList().apply()
  }

  def findAllByUserWithName(memberId: Long)(implicit session: DBSession = Ship.autoSession): List[ShipWithName] = {
    val slots = ShipSlotItem.findAllBy(sqls"member_id = ${memberId}")
    withSQL {
      select.from(Ship as s)
        .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where.eq(s.memberId, memberId)
    }.map { rs =>
      val id = rs.int(s.resultName.id)
      val slot = slots.filter(_.shipId == id).map(_.slotitemId)
      ShipWithName(Ship(s, slot)(rs), MasterShipBase(ms)(rs), MasterStype(mst)(rs))
    }.toList().apply()
  }

  def findSlot(memberId: Long, shipId: Int): List[Int] =
    ShipSlotItem.findAllBy(sqls"member_id = ${memberId} and ship_id = ${shipId}").map(_.slotitemId)

  def create(s: data.Ship, memberId: Long)(implicit session: DBSession = Ship.autoSession): Unit = {
    val created = System.currentTimeMillis()
    ShipSlotItem.bulkInsert(s.slot, memberId, s.id)
    applyUpdate {
      insert.into(Ship).namedValues(
        column.id -> s.id, column.shipId -> s.shipId, column.memberId -> memberId,
        column.lv -> s.lv, column.exp -> s.exp, column.nowhp -> s.nowhp,
        column.dockTime -> s.dockTime, column.cond -> s.cond,
        column.karyoku -> s.karyoku, column.raisou -> s.raisou, column.taiku -> s.taiku, column.soukou -> s.soukou,
        column.kaihi -> s.kaihi, column.taisen -> s.taisen, column.sakuteki -> s.sakuteki, column.lucky -> s.lucky,
        column.locked -> s.locked, column.created -> created,
        column.maxhp -> s.maxhp
      )
    }
  }

  def bulkInsert(ss: Seq[data.Ship], memberId: Long)(
      implicit session: DBSession = Ship.autoSession): Unit = {
    val created = System.currentTimeMillis()
    ShipSlotItem.bulkInserts(ss.map(_.slot), memberId, ss.map(_.id))
    applyUpdate {
      insert.into(Ship).columns(
        column.id, column.shipId, column.memberId,
        column.lv, column.exp, column.nowhp,
        column.fuel, column.bull, column.dockTime, column.cond,
        column.karyoku, column.raisou, column.taiku, column.soukou,
        column.kaihi, column.taisen, column.sakuteki, column.lucky,
        column.locked, column.created, column.maxhp
      ).multiValues(
          ss.map(_.id), ss.map(_.shipId), Seq.fill(ss.size)(memberId),
          ss.map(_.lv), ss.map(_.exp), ss.map(_.nowhp),
          ss.map(_.fuel), ss.map(_.bull), ss.map(_.dockTime), ss.map(_.cond),
          ss.map(_.karyoku), ss.map(_.raisou), ss.map(_.taiku), ss.map(_.soukou),
          ss.map(_.kaihi), ss.map(_.taisen), ss.map(_.sakuteki), ss.map(_.lucky),
          ss.map(_.locked), Seq.fill(ss.size)(created),
          ss.map(_.maxhp)
        )
    }
  }

  def bulkUpsert(ss: Seq[data.Ship], memberId: Long)(implicit sesssion: DBSession = autoSession): Unit = {
    if(ss.isEmpty) return
    ShipSlotItem.deleteAllBy(sqls"member_id = ${memberId} and ship_id in (${ss.map(_.id)})")
    ShipSlotItem.bulkInserts(ss.map(_.slot), memberId, ss.map(_.id))
    val created = System.currentTimeMillis()
    val params = ss.map { x =>
      Seq[Any](
        x.id, x.shipId, memberId, x.lv, x.exp, x.nowhp, x.fuel, x.bull, x.dockTime, x.cond,
        x.karyoku, x.raisou, x.taiku, x.soukou, x.kaihi, x.taisen, x.sakuteki, x.lucky, x.locked, created, x.maxhp
      )
    }
    sql"""replace into ship (id, ship_id, member_id, lv, exp, nowhp, fuel, bull, dock_time, cond,
          karyoku, raisou, taiku, soukou, kaihi, taisen, sakuteki, lucky, locked, created, maxhp)
          values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
      .batch(params:_*).apply()
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
