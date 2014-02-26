package models

import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data
import scalikejdbc.{WrappedResultSet, DBSession}

/**
 *
 * @author ponkotuy
 * Date: 14/02/26.
 */
case class Ship(
    userId: Long, id: Int, shipId: Int,
    lv: Int, exp: Int, nowhp: Int, slot: List[Int], fuel: Int, bull: Int, dockTime: Long, cond: Int,
    karyoku: Int, raisou: Int, taiku: Int, soukou: Int, kaihi: Int, taisen: Int, sakuteki: Int, lucky: Int, locked: Boolean,
    created: Long)

object Ship extends SQLSyntaxSupport[Ship] {
  override val columnNames = Seq("user_id", "id", "ship_id", "lv", "exp", "nowhp", "slot", "fuel", "bull", "dock_time", "cond",
    "karyoku", "raisou", "taiku", "soukou", "kaihi", "taisen", "sakuteki", "lucky", "locked", "created")
  def apply(x: SyntaxProvider[Ship])(rs: WrappedResultSet): Ship = apply(x.resultName)(rs)
  def apply(x: ResultName[Ship])(rs: WrappedResultSet): Ship = new Ship(
    rs.long(x.userId),
    rs.int(x.id),
    rs.int(x.shipId),
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

  def findAllByUser(userId: Long)(implicit session: DBSession = Ship.autoSession): List[Ship] = withSQL {
    select.from(Ship as s)
      .where.eq(s.userId, userId)
  }.map(Ship(s)).toList().apply()

  def findAllByUserWithMaster(userId: Long)(implicit session: DBSession = Ship.autoSession): List[(Ship, MasterShip)] = {
    withSQL {
      select.from(Ship as s)
        .innerJoin(MasterShip as ms).on(s.shipId, ms.id)
        .where.eq(s.userId, userId)
    }.map { rs => (Ship(s)(rs), MasterShip(ms)(rs)) }.toList().apply()
  }

  def create(s: data.Ship, userId: Long)(implicit session: DBSession = Ship.autoSession): Ship = {
    val created = System.currentTimeMillis()
    val slot = s.slot.mkString(",")
    withSQL {
      insert.into(Ship).namedValues(
        column.userId -> userId, column.id -> s.id, column.shipId -> s.shipId,
        column.lv -> s.lv, column.exp -> s.exp, column.nowhp -> s.nowhp, column.slot -> slot,
        column.fuel -> s.fuel, column.bull -> s.bull, column.dockTime -> s.dockTime, column.cond -> s.cond,
        column.karyoku -> s.karyoku, column.raisou -> s.raisou, column.taiku -> s.taiku, column.soukou -> s.soukou,
        column.kaihi -> s.kaihi, column.taisen -> s.taisen, column.sakuteki -> s.sakuteki, column.lucky -> s.lucky,
        column.locked -> s.locked, column.created -> created
      )
    }.update().apply()
    Ship(userId, s.id, s.shipId, s.lv, s.exp, s.nowhp, s.slot, s.fuel, s.bull, s.dockTime, s.cond,
      s.karyoku, s.raisou, s.taiku, s.soukou, s.kaihi, s.taisen, s.sakuteki, s.lucky, s.locked, created
    )
  }

  def deleteAllByUser(userId: Long)(implicit session: DBSession = Ship.autoSession): Unit =
    applyUpdate { delete.from(Ship).where.eq(Ship.column.userId, userId) }
}
