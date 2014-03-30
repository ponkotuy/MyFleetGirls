package models

import scalikejdbc._
import scalikejdbc.SQLInterpolation._
import com.ponkotuy.data

case class CreateItem(
  memberId: Long,
  id: Long,
  itemId: Option[Int] = None,
  slotitemId: Option[Int] = None,
  fuel: Int,
  ammo: Int,
  steel: Int,
  bauxite: Int,
  createFlag: Boolean,
  shizaiFlag: Boolean,
  flagship: Int,
  created: Long) {

  def save()(implicit session: DBSession = CreateItem.autoSession): CreateItem = CreateItem.save(this)(session)

  def destroy()(implicit session: DBSession = CreateItem.autoSession): Unit = CreateItem.destroy(this)(session)

}


object CreateItem extends SQLSyntaxSupport[CreateItem] {

  override val tableName = "create_item"

  override val columns = Seq("member_id", "id", "item_id", "slotitem_id", "fuel", "ammo", "steel", "bauxite", "create_flag", "shizai_flag", "flagship", "created")

  def apply(ci: ResultName[CreateItem])(rs: WrappedResultSet): CreateItem = new CreateItem(
    memberId = rs.long(ci.memberId),
    id = rs.long(ci.id),
    itemId = rs.intOpt(ci.itemId),
    slotitemId = rs.intOpt(ci.slotitemId),
    fuel = rs.int(ci.fuel),
    ammo = rs.int(ci.ammo),
    steel = rs.int(ci.steel),
    bauxite = rs.int(ci.bauxite),
    createFlag = rs.boolean(ci.createFlag),
    shizaiFlag = rs.boolean(ci.shizaiFlag),
    flagship = rs.int(ci.flagship),
    created = rs.long(ci.created)
  )

  val ci = CreateItem.syntax("ci")
  val mi = MasterSlotItem.syntax("mi")
  val s = Ship.syntax("s")
  val ms = MasterShip.syntax("ms")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[CreateItem] = {
    withSQL {
      select.from(CreateItem as ci).where.eq(ci.id, id)
    }.map(CreateItem(ci.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[CreateItem] = {
    withSQL(select.from(CreateItem as ci)).map(CreateItem(ci.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(CreateItem as ci)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[CreateItem] = {
    withSQL {
      select.from(CreateItem as ci).where.append(sqls"${where}")
    }.map(CreateItem(ci.resultName)).list().apply()
  }

  def findAllByUserWithName(memberId: Long, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit sesson: DBSession = autoSession): List[CreateItemWithName] = {
    withSQL {
      select(ci.slotitemId, ci.fuel, ci.ammo, ci.steel, ci.bauxite, ci.shizaiFlag, ci.flagship, ci.created, mi.name, ms.name)
        .from(CreateItem as ci)
        .leftJoin(MasterSlotItem as mi).on(ci.slotitemId, mi.id)
        .leftJoin(Ship as s).on(ci.flagship, s.id)
        .leftJoin(MasterShip as ms).on(s.shipId, ms.id)
        .where.eq(ci.memberId, memberId).and.eq(s.memberId, memberId)
        .orderBy(ci.created).desc
        .limit(limit).offset(offset)
    }.map(CreateItemWithName(ci, mi, ms)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(CreateItem as ci).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(ci: data.CreateItem, memberId: Long)(implicit session: DBSession = autoSession): CreateItem = {
    val now = System.currentTimeMillis()
    createOrig(
      memberId, ci.id,
      ci.slotitemId, ci.fuel, ci.ammo, ci.steel, ci.bauxite, ci.createFlag, ci.shizaiFlag, ci.flagship, now
    )
  }

  def createOrig(
    memberId: Long,
    itemId: Option[Int] = None,
    slotitemId: Option[Int] = None,
    fuel: Int,
    ammo: Int,
    steel: Int,
    bauxite: Int,
    createFlag: Boolean,
    shizaiFlag: Boolean,
    flagship: Int,
    created: Long)(implicit session: DBSession = autoSession): CreateItem = {
    val generatedKey = withSQL {
      insert.into(CreateItem).columns(
        column.memberId,
        column.itemId,
        column.slotitemId,
        column.fuel,
        column.ammo,
        column.steel,
        column.bauxite,
        column.createFlag,
        column.shizaiFlag,
        column.flagship,
        column.created
      ).values(
          memberId,
          itemId,
          slotitemId,
          fuel,
          ammo,
          steel,
          bauxite,
          createFlag,
          shizaiFlag,
          flagship,
          created
        )
    }.updateAndReturnGeneratedKey().apply()

    CreateItem(
      id = generatedKey,
      memberId = memberId,
      itemId = itemId,
      slotitemId = slotitemId,
      fuel = fuel,
      ammo = ammo,
      steel = steel,
      bauxite = bauxite,
      createFlag = createFlag,
      shizaiFlag = shizaiFlag,
      flagship = flagship,
      created = created
    )
  }

  def save(entity: CreateItem)(implicit session: DBSession = autoSession): CreateItem = {
    withSQL {
      update(CreateItem).set(
        column.memberId -> entity.memberId,
        column.id -> entity.id,
        column.itemId -> entity.itemId,
        column.slotitemId -> entity.slotitemId,
        column.fuel -> entity.fuel,
        column.ammo -> entity.ammo,
        column.steel -> entity.steel,
        column.bauxite -> entity.bauxite,
        column.createFlag -> entity.createFlag,
        column.shizaiFlag -> entity.shizaiFlag,
        column.flagship -> entity.flagship,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: CreateItem)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(CreateItem).where.eq(column.id, entity.id)
    }.update().apply()
  }

}

case class CreateItemWithName(
    slotitemId: Option[Int], fuel: Int, ammo: Int, steel: Int, bauxite: Int,
    shizaiFlag: Boolean, flagshipId: Int, created: Long, name: String, flagshipName: String)

object CreateItemWithName {
  def apply(ci: SyntaxProvider[CreateItem], mi: SyntaxProvider[MasterSlotItem], ms: SyntaxProvider[MasterShip])(
      rs: WrappedResultSet): CreateItemWithName =
    new CreateItemWithName(
      rs.intOpt(ci.slotitemId),
      rs.int(ci.fuel),
      rs.int(ci.ammo),
      rs.int(ci.steel),
      rs.int(ci.bauxite),
      rs.boolean(ci.shizaiFlag),
      rs.int(ci.flagship),
      rs.long(ci.created),
      rs.stringOpt(mi.name).getOrElse("失敗"),
      rs.string(ms.name)
    )
}
