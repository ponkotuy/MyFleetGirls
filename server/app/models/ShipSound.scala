package models

import SQLInterpolation._
import scalikejdbc.{AutoSession, WrappedResultSet, DBSession}

case class ShipSound(
  shipId: Int,
  soundId: Int,
  sound: Array[Byte]) {

  def save()(implicit session: DBSession = ShipSound.autoSession): ShipSound = ShipSound.save(this)(session)

  def destroy()(implicit session: DBSession = ShipSound.autoSession): Unit = ShipSound.destroy(this)(session)

}


object ShipSound extends SQLSyntaxSupport[ShipSound] {

  override val tableName = "ship_sound"

  override val columns = Seq("ship_id", "sound_id", "sound")

  def apply(ss: ResultName[ShipSound])(rs: WrappedResultSet): ShipSound = new ShipSound(
    shipId = rs.int(ss.shipId),
    soundId = rs.int(ss.soundId),
    sound = rs.bytes(ss.sound)
  )

  val ss = ShipSound.syntax("ss")

  override val autoSession = AutoSession

  def find(shipId: Int, soundId: Int)(implicit session: DBSession = autoSession): Option[ShipSound] = {
    withSQL {
      select.from(ShipSound as ss).where.eq(ss.shipId, shipId).and.eq(ss.soundId, soundId)
    }.map(ShipSound(ss.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ShipSound] = {
    withSQL(select.from(ShipSound as ss)).map(ShipSound(ss.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(ShipSound as ss)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ShipSound] = {
    withSQL {
      select.from(ShipSound as ss).where.append(sqls"${where}")
    }.map(ShipSound(ss.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(ShipSound as ss).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    shipId: Int,
    soundId: Int,
    sound: Array[Byte])(implicit session: DBSession = autoSession): ShipSound = {
    withSQL {
      insertIgnore.into(ShipSound).columns(
        column.shipId,
        column.soundId,
        column.sound
      ).values(
          shipId,
          soundId,
          sound
        )
    }.update().apply()

    ShipSound(
      shipId = shipId,
      soundId = soundId,
      sound = sound)
  }

  def save(entity: ShipSound)(implicit session: DBSession = autoSession): ShipSound = {
    withSQL {
      update(ShipSound).set(
        column.shipId -> entity.shipId,
        column.soundId -> entity.soundId,
        column.sound -> entity.sound
      ).where.eq(column.shipId, entity.shipId).and.eq(column.soundId, entity.soundId)
    }.update().apply()
    entity
  }

  def destroy(entity: ShipSound)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(ShipSound).where.eq(column.shipId, entity.shipId).and.eq(column.soundId, entity.soundId)
    }.update().apply()
  }

}
