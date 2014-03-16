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
  flagship: Int) {

  def save()(implicit session: DBSession = CreateItem.autoSession): CreateItem = CreateItem.save(this)(session)

  def destroy()(implicit session: DBSession = CreateItem.autoSession): Unit = CreateItem.destroy(this)(session)

}
      

object CreateItem extends SQLSyntaxSupport[CreateItem] {

  override val tableName = "create_item"

  override val columns = Seq("member_id", "id", "item_id", "slotitem_id", "fuel", "ammo", "steel", "bauxite", "create_flag", "shizai_flag", "flagship")

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
    flagship = rs.int(ci.flagship)
  )
      
  val ci = CreateItem.syntax("ci")

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
      
  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(CreateItem as ci).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(ci: data.CreateItem, memberId: Long)(implicit session: DBSession = autoSession): CreateItem =
    createOrig(memberId, ci.id, ci.slotitemId, ci.fuel, ci.ammo, ci.steel, ci.bauxite, ci.createFlag, ci.shizaiFlag, ci.flagship)

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
    flagship: Int)(implicit session: DBSession = autoSession): CreateItem = {
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
        column.flagship
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
          flagship
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
      flagship = flagship)
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
        column.flagship -> entity.flagship
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
