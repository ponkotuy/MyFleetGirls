package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{WrappedResultSet, DBSession}
import com.ponkotuy.data
import util.scalikejdbc.BulkInsert._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._

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
  override val columnNames = Seq("id", "ship_id", "member_id", "lv", "exp", "nowhp", "slot", "fuel", "bull", "dock_time", "cond",
    "karyoku", "raisou", "taiku", "soukou", "kaihi", "taisen", "sakuteki", "lucky", "locked", "created")
  def apply(x: SyntaxProvider[Ship])(rs: WrappedResultSet): Ship = apply(x.resultName)(rs)
  def apply(x: ResultName[Ship])(rs: WrappedResultSet): Ship = new Ship(
    rs.int(x.id),
    rs.int(x.shipId),
    rs.long(x.memberId),
    rs.int(x.lv),
    rs.int(x.exp),
    rs.int(x.nowhp),
    rs.string(x.slot).split(',').map(_.toInt).toList,
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

  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShip.syntax("ms")
  lazy val ds = DeckShip.syntax("ds")

  def findByUserMaxLvWithName(memberId: Long)(implicit session: DBSession = autoSession): Option[ShipWithName] = {
    withSQL {
      select.from(Ship as s)
        .leftJoin(MasterShip as ms).on(s.shipId, ms.id)
        .where.eq(s.memberId, memberId).and.eq(s.lv, sqls"(SELECT MAX(${s.lv}) FROM ${Ship.table} s WHERE ${s.memberId} = ${memberId})")
    }.map { rs => ShipWithName(Ship(s)(rs), MasterShip(ms)(rs)) }
      .first().apply()
  }

  def findAllByUser(memberId: Long)(implicit session: DBSession = Ship.autoSession): List[Ship] = withSQL {
    select.from(Ship as s)
      .where.eq(s.memberId, memberId)
  }.map(Ship(s)).toList().apply()

  def findAllByUserWithName(memberId: Long)(implicit session: DBSession = Ship.autoSession): List[ShipWithName] = {
    withSQL {
      select.from(Ship as s)
        .leftJoin(MasterShip as ms).on(s.shipId, ms.id)
        .where.eq(s.memberId, memberId)
    }.map { rs => ShipWithName(Ship(s)(rs), MasterShip(ms)(rs)) }.toList().apply()
  }

  def create(s: data.Ship, memberId: Long)(implicit session: DBSession = Ship.autoSession): Ship = {
    val created = System.currentTimeMillis()
    val slot = s.slot.mkString(",")
    withSQL {
      insert.into(Ship).namedValues(
        column.id -> s.id, column.shipId -> s.shipId, column.memberId -> memberId,
        column.lv -> s.lv, column.exp -> s.exp, column.nowhp -> s.nowhp, column.slot -> slot,
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
    val slots = ss.map(_.slot.mkString(","))
    applyUpdate {
      insert.into(Ship).columns(
        column.id, column.shipId, column.memberId,
        column.lv, column.exp, column.nowhp, column.slot,
        column.fuel, column.bull, column.dockTime, column.cond,
        column.karyoku, column.raisou, column.taiku, column.soukou,
        column.kaihi, column.taisen, column.sakuteki, column.lucky,
        column.locked, column.created
      ).multiValues(
          ss.map(_.id), ss.map(_.shipId), Seq.fill(ss.size)(memberId),
          ss.map(_.lv), ss.map(_.exp), ss.map(_.nowhp), slots,
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

  def deleteAllByUser(memberId: Long)(implicit session: DBSession = Ship.autoSession): Unit =
    applyUpdate { delete.from(Ship).where.eq(Ship.column.memberId, memberId) }
}

case class ShipWithName(ship: Ship, master: MasterShip) {
  def id = ship.id
  def shipId = ship.shipId
  def memberId = ship.memberId
  def lv = ship.lv
  def exp = ship.exp
  def nowhp = ship.nowhp
  def slot = ship.slot
  def fuel = ship.fuel
  def bull = ship.bull
  def dockTime = ship.dockTime
  def cond = ship.cond
  def karyoku = ship.karyoku
  def raisou = ship.raisou
  def taiku = ship.taiku
  def soukou = ship.soukou
  def kaihi = ship.kaihi
  def taisen = ship.taisen
  def sakuteki = ship.sakuteki
  def lucky = ship.lucky
  def locked = ship.locked
  def created = ship.created
  def name = master.name
  def yomi = master.yomi

  def slotNames: List[String] = MasterSlotItem.names(slot)

  def toJson: String = {
    println(slot)
    println(slotNames)
    val seq = Seq(
      Seq(0, karyoku / 150.0),
      Seq(1, raisou / 130.0),
      Seq(2, taiku / 90.0),
      Seq(3, soukou / 100.0),
      Seq(4, kaihi / 80.0),
      Seq(5, taisen / 80.0),
      Seq(6, sakuteki / 70.0),
      Seq(7, lucky / 40.0)
    )
    compact(render(seq))
  }
}
