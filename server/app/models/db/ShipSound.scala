package models.db

import scalikejdbc._

import scala.util.Random

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

  def apply(ss: ResultName[ShipSound])(rs: WrappedResultSet): ShipSound = autoConstruct(rs, ss)

  lazy val ss = ShipSound.syntax("ss")
  lazy val ms = MasterShipBase.syntax("ms")

  override val autoSession = AutoSession

  def find(shipId: Int, soundId: Int)(implicit session: DBSession = autoSession): Option[ShipSound] = {
    withSQL {
      select.from(ShipSound as ss).where.eq(ss.shipId, shipId).and.eq(ss.soundId, soundId)
    }.map(ShipSound(ss.resultName)).single().apply()
  }

  def findKey(shipKey: String, soundId: Int)(implicit session: DBSession = autoSession): Option[ShipSound] = {
    withSQL {
      select.from(ShipSound as ss)
        .innerJoin(MasterShipBase as ms).on(ss.shipId, ms.id)
        .where.eq(ms.filename, shipKey).and.eq(ss.soundId, soundId)
    }.map(ShipSound(ss.resultName)).single().apply()
  }

  def findRandom()(implicit session: DBSession = autoSession): ShipSound = {
    val count = countAll()
    val rand = Random.nextInt(count.toInt - 1)
    withSQL {
      select.from(ShipSound as ss)
        .limit(1).offset(rand)
    }.map(ShipSound(ss.resultName)).single().apply().get
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
    sql"insert ignore into ship_sound (${column.shipId}, ${column.soundId}, ${column.sound}) values (${shipId}, ${soundId}, ${sound})"
      .update().apply()
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
