package models.db

import scalikejdbc._

import scala.util.Random

case class ShipSound(
    shipId: Int,
    soundId: Int,
    sound: Array[Byte],
    version: Int) {

  def save()(implicit session: DBSession = ShipSound.autoSession): ShipSound = ShipSound.save(this)(session)

  def destroy()(implicit session: DBSession = ShipSound.autoSession): Unit = ShipSound.destroy(this)(session)

}


object ShipSound extends SQLSyntaxSupport[ShipSound] {

  override val tableName = "ship_sound"

  override val columns = Seq("ship_id", "sound_id", "sound", "version")

  def apply(ss: ResultName[ShipSound])(rs: WrappedResultSet): ShipSound = autoConstruct(rs, ss)

  val ss = ShipSound.syntax("ss")
  val ms = MasterShipBase.syntax("ms")

  override val autoSession = AutoSession

  def find(shipId: Int, soundId: Int, version: Int)(implicit session: DBSession = autoSession): Option[ShipSound] = {
    withSQL {
      select.from(ShipSound as ss)
          .where.eq(ss.shipId, shipId).and.eq(ss.soundId, soundId).and.eq(ss.version, version)
    }.map(ShipSound(ss.resultName)).single().apply()
  }

  def findKey(shipKey: String, soundId: Int, version: Int)(implicit session: DBSession = autoSession): Option[ShipSound] = {
    withSQL {
      select.from(ShipSound as ss)
        .innerJoin(MasterShipBase as ms).on(ss.shipId, ms.id)
        .where.eq(ms.filename, shipKey).and.eq(ss.soundId, soundId).and.eq(ss.version, version)
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

  def findRandomBy(shipId: Int, soundId: Int)(implicit session: DBSession = autoSession): Option[ShipSound] = {
    withSQL {
      select.from(ShipSound as ss)
          .where.eq(ss.shipId, shipId).and.eq(ss.soundId, soundId)
          .orderBy(sqls"rand()").limit(1)
    }.map(ShipSound(ss.resultName)).single().apply()
  }

  def findRandomKey(shipKey: String, soundId: Int)(implicit session: DBSession = autoSession): Option[ShipSound] = {
    withSQL {
      select.from(ShipSound as ss)
          .innerJoin(MasterShipBase as ms).on(ss.shipId, ms.id)
          .where.eq(ms.filename, shipKey).and.eq(ss.soundId, soundId)
          .orderBy(sqls"rand()").limit(1)
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
      version: Int,
      sound: Array[Byte])(implicit session: DBSession = autoSession): Unit = {
    sql"insert ignore into ship_sound (${column.shipId}, ${column.soundId}, ${column.sound}, ${column.version}) values (${shipId}, ${soundId}, ${sound}, ${version})"
      .update().apply()
  }

  def save(entity: ShipSound)(implicit session: DBSession = autoSession): ShipSound = {
    withSQL {
      update(ShipSound).set(
        column.shipId -> entity.shipId,
        column.soundId -> entity.soundId,
        column.sound -> entity.sound,
        column.version -> entity.version
      ).where.eq(column.shipId, entity.shipId).and.eq(column.soundId, entity.soundId).and.eq(column.version, entity.version)
    }.update().apply()
    entity
  }

  def destroy(entity: ShipSound)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(ShipSound)
          .where.eq(column.shipId, entity.shipId)
            .and.eq(column.soundId, entity.soundId)
            .and.eq(column.version, entity.version)
    }.update().apply()
  }

}
