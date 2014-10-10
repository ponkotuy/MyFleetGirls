package models

import com.ponkotuy.data.master
import scalikejdbc._
import util.scalikejdbc.BulkInsert._

case class MasterShipOther(
  id: Int,
  buildtime: Int,
  brokenFuel: Int,
  brokenAmmo: Int,
  brokenSteel: Int,
  brokenBauxite: Int,
  powupFuel: Int,
  powupAmmo: Int,
  powupSteel: Int,
  powupBauxite: Int,
  backs: Int,
  fuelMax: Int,
  bullMax: Int,
  slotNum: Int) {

  def save()(implicit session: DBSession = MasterShipOther.autoSession): MasterShipOther = MasterShipOther.save(this)(session)

  def destroy()(implicit session: DBSession = MasterShipOther.autoSession): Unit = MasterShipOther.destroy(this)(session)

}


object MasterShipOther extends SQLSyntaxSupport[MasterShipOther] {

  override val tableName = "master_ship_other"

  override val columns = Seq("id", "buildtime", "broken_fuel", "broken_ammo", "broken_steel", "broken_bauxite", "powup_fuel", "powup_ammo", "powup_steel", "powup_bauxite", "backs", "fuel_max", "bull_max", "slot_num")

  def apply(mso: ResultName[MasterShipOther])(rs: WrappedResultSet): MasterShipOther = new MasterShipOther(
    id = rs.int(mso.id),
    buildtime = rs.int(mso.buildtime),
    brokenFuel = rs.int(mso.brokenFuel),
    brokenAmmo = rs.int(mso.brokenAmmo),
    brokenSteel = rs.int(mso.brokenSteel),
    brokenBauxite = rs.int(mso.brokenBauxite),
    powupFuel = rs.int(mso.powupFuel),
    powupAmmo = rs.int(mso.powupAmmo),
    powupSteel = rs.int(mso.powupSteel),
    powupBauxite = rs.int(mso.powupBauxite),
    backs = rs.int(mso.backs),
    fuelMax = rs.int(mso.fuelMax),
    bullMax = rs.int(mso.bullMax),
    slotNum = rs.int(mso.slotNum)
  )

  val mso = MasterShipOther.syntax("mso")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[MasterShipOther] = {
    withSQL {
      select.from(MasterShipOther as mso).where.eq(mso.id, id)
    }.map(MasterShipOther(mso.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MasterShipOther] = {
    withSQL(select.from(MasterShipOther as mso)).map(MasterShipOther(mso.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MasterShipOther as mso)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterShipOther] = {
    withSQL {
      select.from(MasterShipOther as mso).where.append(sqls"${where}")
    }.map(MasterShipOther(mso.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MasterShipOther as mso).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    id: Int,
    buildtime: Int,
    brokenFuel: Int,
    brokenAmmo: Int,
    brokenSteel: Int,
    brokenBauxite: Int,
    powupFuel: Int,
    powupAmmo: Int,
    powupSteel: Int,
    powupBauxite: Int,
    backs: Int,
    fuelMax: Int,
    bullMax: Int,
    slotNum: Int)(implicit session: DBSession = autoSession): MasterShipOther = {
    withSQL {
      insert.into(MasterShipOther).columns(
        column.id,
        column.buildtime,
        column.brokenFuel,
        column.brokenAmmo,
        column.brokenSteel,
        column.brokenBauxite,
        column.powupFuel,
        column.powupAmmo,
        column.powupSteel,
        column.powupBauxite,
        column.backs,
        column.fuelMax,
        column.bullMax,
        column.slotNum
      ).values(
          id,
          buildtime,
          brokenFuel,
          brokenAmmo,
          brokenSteel,
          brokenBauxite,
          powupFuel,
          powupAmmo,
          powupSteel,
          powupBauxite,
          backs,
          fuelMax,
          bullMax,
          slotNum
        )
    }.update().apply()

    MasterShipOther(
      id = id,
      buildtime = buildtime,
      brokenFuel = brokenFuel,
      brokenAmmo = brokenAmmo,
      brokenSteel = brokenSteel,
      brokenBauxite = brokenBauxite,
      powupFuel = powupFuel,
      powupAmmo = powupAmmo,
      powupSteel = powupSteel,
      powupBauxite = powupBauxite,
      backs = backs,
      fuelMax = fuelMax,
      bullMax = bullMax,
      slotNum = slotNum)
  }

  def bulkInsert(xs: Seq[master.MasterShipOther])(implicit session: DBSession = autoSession): Unit = {
    require(xs.nonEmpty)
    applyUpdate {
      insert.into(MasterShipOther)
        .columns(
          column.id, column.buildtime,
          column.brokenFuel, column.brokenAmmo, column.brokenSteel, column.brokenBauxite,
          column.powupFuel, column.powupAmmo, column.powupSteel, column.powupBauxite,
          column.backs, column.fuelMax, column.bullMax, column.slotNum
        )
        .multiValues(
          xs.map(_.id), xs.map(_.buildtime),
          xs.map(_.brokenFuel), xs.map(_.brokenAmmo), xs.map(_.brokenSteel), xs.map(_.brokenBauxite),
          xs.map(_.powupFuel), xs.map(_.powupAmmo), xs.map(_.powupSteel), xs.map(_.powupBauxite),
          xs.map(_.backs), xs.map(_.fuelMax), xs.map(_.bullMax), xs.map(_.slotNum)
        )
    }
  }

  def save(entity: MasterShipOther)(implicit session: DBSession = autoSession): MasterShipOther = {
    withSQL {
      update(MasterShipOther).set(
        column.id -> entity.id,
        column.buildtime -> entity.buildtime,
        column.brokenFuel -> entity.brokenFuel,
        column.brokenAmmo -> entity.brokenAmmo,
        column.brokenSteel -> entity.brokenSteel,
        column.brokenBauxite -> entity.brokenBauxite,
        column.powupFuel -> entity.powupFuel,
        column.powupAmmo -> entity.powupAmmo,
        column.powupSteel -> entity.powupSteel,
        column.powupBauxite -> entity.powupBauxite,
        column.backs -> entity.backs,
        column.fuelMax -> entity.fuelMax,
        column.bullMax -> entity.bullMax,
        column.slotNum -> entity.slotNum
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def deleteAll()(implicit session: DBSession = autoSession): Unit = applyUpdate {
    delete.from(MasterShipOther)
  }

  def destroy(entity: MasterShipOther)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MasterShipOther).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
