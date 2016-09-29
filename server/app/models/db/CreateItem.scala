package models.db

import com.ponkotuy.data
import models.join.{ItemWithAdmiral, ItemMat, CreateItemWithName}
import scalikejdbc._
import scalikejdbc.interpolation.SQLSyntax._
import com.github.nscala_time.time.Imports._

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
  import util.MFGDateUtil._

  override val tableName = "create_item"

  override val columns = Seq("member_id", "id", "item_id", "slotitem_id", "fuel", "ammo", "steel", "bauxite", "create_flag", "shizai_flag", "flagship", "created")

  def apply(ci: ResultName[CreateItem])(rs: WrappedResultSet): CreateItem = autoConstruct(rs, ci)

  val ci = CreateItem.syntax("ci")
  val mi = MasterSlotItem.syntax("mi")
  val s = Ship.syntax("s")
  val ms = MasterShipBase.syntax("ms")
  val mst = MasterStype.syntax("mst")
  val a = Admiral.syntax("a")

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
        .orderBy(ci.created).desc
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
  def countItemByMat(mat: ItemMat, where: SQLSyntax = sqls"true")(implicit session: DBSession = autoSession): List[(MiniItem, Long)] = {
    withSQL {
      select(ci.slotitemId, mi.name, sqls"count(*) as count").from(CreateItem as ci)
        .innerJoin(Ship as s).on(sqls"ci.flagship = s.id and ci.member_id = s.member_id")
        .innerJoin(MasterShipBase as ms).on(s.shipId, ms.id)
        .leftJoin(MasterSlotItem as mi).on(ci.slotitemId, mi.id) // slotitemが無しのときでも消えないようにleftjoin
        .innerJoin(MasterStype as mst).on(ms.stype, mst.id)
        .where(sqls"""ci.fuel = ${mat.fuel} and ci.ammo = ${mat.ammo} and ci.steel = ${mat.steel} and ci.bauxite = ${mat.bauxite} and mst.name = ${mat.sTypeName}""")
        .and.append(where)
        .groupBy(ci.slotitemId)
        .orderBy(sqls"count").desc
    }.map { rs => MiniItem.fromRS(rs) -> rs.long(3) }.list().apply()
  }

  var materialCountCache: List[(ItemMat, Long)] = Nil
  val CachePeriod = 1.day
  var cacheDate = new DateTime(0L)

  def materialCount(where: Option[SQLSyntax] = None)(implicit session: DBSession = autoSession): List[(ItemMat, Long)] = {
    if(where.isEmpty) {
      val now = DateTime.now(Tokyo)
      if(cacheDate + CachePeriod < now) {
        materialCountCache = materialCountFromDB(where)
        cacheDate = now
      }
      materialCountCache
    } else {
      materialCountFromDB(where)
    }
  }

  private def materialCountFromDB(where: Option[SQLSyntax])(implicit session: DBSession = autoSession): List[(ItemMat, Long)] =
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

  def existsItem(itemId: Int)(implicit session: DBSession = autoSession): Boolean = withSQL {
    select(ci.slotitemId).from(CreateItem as ci).where.eq(ci.slotitemId, itemId).limit(1)
  }.map(_ => true).single().apply().isDefined

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

case class MiniItem(itemId: Int, name: String)

object MiniItem {
  def fromRS(rs: WrappedResultSet): MiniItem = {
    rs.intOpt(1) match {
      case Some(itemId) =>
        val name = rs.string(2)
        MiniItem(itemId, name)
      case _ => MiniItem(-1, "失敗")
    }
  }
}
