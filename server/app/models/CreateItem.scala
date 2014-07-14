package models

import com.ponkotuy.data
import dat.{ItemWithAdmiral, CreateItemWithName, ItemMat}
import scalikejdbc._
import scalikejdbc.interpolation.SQLSyntax._

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

  lazy val ci = CreateItem.syntax("ci")
  lazy val mi = MasterSlotItem.syntax("mi")
  lazy val s = Ship.syntax("s")
  lazy val ms = MasterShipBase.syntax("ms")
  lazy val mst = MasterStype.syntax("mst")
  lazy val a = Admiral.syntax("a")

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

  def findAllByWithName(where: SQLSyntax, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit sesson: DBSession = autoSession): List[CreateItemWithName] = {
    withSQL {
      select(ci.slotitemId, ci.fuel, ci.ammo, ci.steel, ci.bauxite, ci.shizaiFlag, ci.flagship, ci.created, mi.name, ms.name)
        .from(CreateItem as ci)
        .leftJoin(MasterSlotItem as mi).on(ci.slotitemId, mi.id)
        .leftJoin(Ship as s).on(sqls"ci.flagship = s.id and ci.member_id = s.member_id")
        .leftJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where(where)
        .orderBy(ci.created).desc
        .limit(limit).offset(offset)
    }.map(CreateItemWithName(ci, mi, ms)).list().apply()
  }

  def findAllItemByNameLike(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MasterSlotItem] = {
    withSQL {
      select(distinct(mi.resultAll)).from(CreateItem as ci)
        .innerJoin(MasterSlotItem as mi).on(ci.slotitemId, mi.id)
        .where(where)
    }.map(MasterSlotItem(mi)).toList().apply()
  }

  def findWithUserBy(where: SQLSyntax, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[ItemWithAdmiral] = {
    withSQL {
      select.from(CreateItem as ci)
        .innerJoin(Admiral as a).on(ci.memberId, a.id)
        .innerJoin(MasterSlotItem as mi).on(ci.slotitemId, mi.id)
        .where(where)
        .orderBy(ci.created)
        .limit(limit).offset(offset)
    }.map(ItemWithAdmiral(ci, a, mi)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(CreateItem as ci).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  /**
   *
   * @param mat : sTypeNameは無視する
   */
  def countItemByMat(mat: ItemMat)(implicit session: DBSession = autoSession): List[(MiniItem, Long)] = {
    withSQL {
      select(ci.slotitemId, mi.name, sqls"count(*) as count").from(CreateItem as ci)
        .innerJoin(Ship as s).on(sqls"ci.flagship = s.id and ci.member_id = s.member_id")
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterSlotItem as mi).on(ci.slotitemId, mi.id)
        .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where(sqls"""ci.fuel = ${mat.fuel} and ci.ammo = ${mat.ammo} and ci.steel = ${mat.steel} and ci.bauxite = ${mat.bauxite} and mst.name = ${mat.sTypeName}""")
        .groupBy(ci.slotitemId)
        .orderBy(sqls"count").desc
    }.map { rs => fromRStoMiniItem(rs) -> rs.long(3) }.list().apply()
  }

  def materialCount(where: SQLSyntax = sqls"true")(implicit session: DBSession = autoSession): List[(ItemMat, Long)] =
    withSQL {
      select(ci.fuel, ci.ammo, ci.steel, ci.bauxite, mst.id, mst.name, sqls"count(*) as count")
        .from(CreateItem as ci)
        .innerJoin(Ship as s).on(sqls"(ci.flagship = s.id and ci.member_id = s.member_id)")
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where(where)
        .groupBy(ci.fuel, ci.ammo, ci.steel, ci.bauxite, mst.name)
        .orderBy(sqls"count").desc
    }.map { rs =>
      ItemMat(ci, mst)(rs) -> rs.long(7)
    }.list().apply()

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

  def fromRStoMiniItem(rs: WrappedResultSet): MiniItem = {
    rs.intOpt(1) match {
      case Some(itemId) =>
        val name = rs.string(2)
        MiniItem(itemId, name)
      case _ => MiniItem(-1, "失敗")
    }
  }

}
