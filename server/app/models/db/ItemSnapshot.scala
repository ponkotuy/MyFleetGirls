package models.db

import scalikejdbc._

case class ItemSnapshot(
  id: Long,
  memberId: Long,
  shipSnapshotId: Long,
  position: Int,
  slotitemId: Int,
  level: Int,
  alv: Option[Int] = None,
  created: Long) {

  def save()(implicit session: DBSession = ItemSnapshot.autoSession): ItemSnapshot = ItemSnapshot.save(this)(session)

  def destroy()(implicit session: DBSession = ItemSnapshot.autoSession): Unit = ItemSnapshot.destroy(this)(session)

}


object ItemSnapshot extends SQLSyntaxSupport[ItemSnapshot] {

  override val tableName = "item_snapshot"

  override val columns = Seq("id", "member_id", "ship_snapshot_id", "position", "slotitem_id", "level", "alv", "created")

  def apply(is: SyntaxProvider[ItemSnapshot])(rs: WrappedResultSet): ItemSnapshot = autoConstruct(rs, is)
  def apply(is: ResultName[ItemSnapshot])(rs: WrappedResultSet): ItemSnapshot = autoConstruct(rs, is)

  val is = ItemSnapshot.syntax("isnp")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[ItemSnapshot] = {
    withSQL {
      select.from(ItemSnapshot as is).where.eq(is.id, id)
    }.map(ItemSnapshot(is.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ItemSnapshot] = {
    withSQL(select.from(ItemSnapshot as is)).map(ItemSnapshot(is.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(ItemSnapshot as is)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[ItemSnapshot] = {
    withSQL {
      select.from(ItemSnapshot as is).where.append(where)
    }.map(ItemSnapshot(is.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ItemSnapshot] = {
    withSQL {
      select.from(ItemSnapshot as is).where.append(where)
    }.map(ItemSnapshot(is.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(ItemSnapshot as is).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    memberId: Long,
    shipSnapshotId: Long,
    position: Int,
    slotitemId: Int,
    level: Int,
    alv: Option[Int] = None,
    created: Long)(implicit session: DBSession = autoSession): ItemSnapshot = {
    val generatedKey = withSQL {
      insert.into(ItemSnapshot).columns(
        column.memberId,
        column.shipSnapshotId,
        column.position,
        column.slotitemId,
        column.level,
        column.alv,
        column.created
      ).values(
        memberId,
        shipSnapshotId,
        position,
        slotitemId,
        level,
        alv,
        created
      )
    }.updateAndReturnGeneratedKey.apply()

    ItemSnapshot(
      id = generatedKey,
      memberId = memberId,
      shipSnapshotId = shipSnapshotId,
      position = position,
      slotitemId = slotitemId,
      level = level,
      alv = alv,
      created = created)
  }

  def batchInsert(entities: Seq[ItemSnapshot])(implicit session: DBSession = autoSession): Seq[Int] = {
    if(entities.isEmpty) return Nil
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'memberId -> entity.memberId,
        'shipSnapshotId -> entity.shipSnapshotId,
        'position -> entity.position,
        'slotitemId -> entity.slotitemId,
        'level -> entity.level,
        'alv -> entity.alv,
        'created -> entity.created))
        SQL("""insert into item_snapshot(
        member_id,
        ship_snapshot_id,
        position,
        slotitem_id,
        level,
        alv,
        created
      ) values (
        {memberId},
        {shipSnapshotId},
        {position},
        {slotitemId},
        {level},
        {alv},
        {created}
      )""").batchByName(params: _*).apply()
    }

  def save(entity: ItemSnapshot)(implicit session: DBSession = autoSession): ItemSnapshot = {
    withSQL {
      update(ItemSnapshot).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.shipSnapshotId -> entity.shipSnapshotId,
        column.position -> entity.position,
        column.slotitemId -> entity.slotitemId,
        column.level -> entity.level,
        column.alv -> entity.alv,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: ItemSnapshot)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(ItemSnapshot).where.eq(column.id, entity.id) }.update.apply()
  }

  def destroyBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Unit = {
    applyUpdate { delete.from(ItemSnapshot).where(where) }
  }

}
