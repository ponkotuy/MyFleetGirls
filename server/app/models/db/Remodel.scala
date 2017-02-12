package models.db

import models.join.RemodelWithName
import scalikejdbc._
import com.ponkotuy.data

import scala.util.Try

case class Remodel(
  id: Long,
  memberId: Long,
  flag: Boolean,
  beforeItemId: Int,
  afterItemId: Int,
  vocieId: Int,
  useSlotIds: String,
  certain: Boolean,
  beforeItemLevel: Int,
  firstShipId: Int,
  secondShipId: Option[Int],
  created: Long) {

  def save()(implicit session: DBSession = Remodel.autoSession): Remodel = Remodel.save(this)(session)

  def destroy()(implicit session: DBSession = Remodel.autoSession): Unit = Remodel.destroy(this)(session)

}


object Remodel extends SQLSyntaxSupport[Remodel] {

  override val tableName = "remodel"

  override val columns = Seq("id", "member_id", "flag", "before_item_id", "after_item_id", "vocie_id", "use_slot_ids", "certain", "before_item_level", "first_ship_id", "second_ship_id", "created")

  def apply(r: SyntaxProvider[Remodel])(rs: WrappedResultSet): Remodel = apply(r.resultName)(rs)
  def apply(r: ResultName[Remodel])(rs: WrappedResultSet): Remodel = autoConstruct(rs, r)

  val r = Remodel.syntax("r")
  val before = MasterSlotItem.syntax("bef")
  val after = MasterSlotItem.syntax("aft")
  val ras = RemodelAfterSlot.syntax("ras")
  val mr = MasterRemodel.syntax("mr")
  val ms = MasterShipBase.syntax("ms")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[Remodel] = {
    withSQL {
      select.from(Remodel as r).where.eq(r.id, id)
    }.map(Remodel(r.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Remodel] = {
    withSQL(select.from(Remodel as r)).map(Remodel(r.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(Remodel as r)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Remodel] = {
    withSQL {
      select.from(Remodel as r).where.append(sqls"${where}")
    }.map(Remodel(r.resultName)).list().apply()
  }

  def findAllByWithName(
      where: SQLSyntax,
      limit: Int = 10,
      offset: Int = 0)(implicit session: DBSession = autoSession): List[RemodelWithName] =
    withSQL {
      select.from(Remodel as r)
        .innerJoin(MasterSlotItem as before).on(r.beforeItemId, before.id)
        .innerJoin(MasterSlotItem as after).on(r.afterItemId, after.id)
        .leftJoin(RemodelAfterSlot as ras).on(r.id, ras.remodelId)
        .leftJoin(MasterShipBase as ms).on(r.secondShipId, ms.id)
        .where(where).orderBy(r.created).desc
        .limit(limit).offset(offset)
    }.map { rs =>
      val remodel = Remodel(r)(rs)
      val bItem = MasterSlotItem(before)(rs)
      val aItem = MasterSlotItem(after)(rs)
      val aSlot = Try { RemodelAfterSlot(ras)(rs) }.toOption
      val secondShip = Try { MasterShipBase(ms)(rs) }.toOption
      RemodelWithName(remodel, bItem, aItem, aSlot, secondShip)
    }.list().apply()

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(Remodel as r).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def countAllFromBefore(where: SQLSyntax = sqls"true")(implicit dBSession: DBSession = autoSession): Map[Int, Long] = {
    withSQL {
      select(r.beforeItemId, sqls.count).from(Remodel as r)
        .where(where).groupBy(r.beforeItemId)
    }.map { rs =>
      rs.int(1) -> rs.long(2)
    }.list().apply().toMap
  }

  def create(x: data.Remodel, memberId: Long)(implicit session: DBSession = autoSession): Option[Long] = {
    /*
     * 本来正確な旗艦と2番艦のデータを持っているのはClientなので、data.Remodelにはそれらが入っているべき
     * 歴史的事情で昔入ってなかったので、無いときは補完をする
     */
    val firstShipId = x.firstShip.getOrElse {
      val deck = DeckShip.find(memberId, 1, 0).get.shipId // 第一艦隊旗艦は必ずいる
      Ship.find(memberId, deck).get.shipId
    }
    val secondShipId = x.secondShip.orElse {
      for {
        secondShipDeck <- DeckShip.find(memberId, 1, 1)
        deckId = secondShipDeck.shipId
        secondShip <- Ship.find(memberId, deckId)
        shipId = secondShip.shipId
      } yield shipId
    }
    val now = System.currentTimeMillis()
    SlotItem.find(x.slotId, memberId).map { beforeItem =>
      val key = createOrig(memberId, x.flag, x.beforeItemId, x.afterItemId, x.voiceId, x.useSlotIds.mkString(","), x.certain, beforeItem.level, firstShipId, secondShipId, now)
      x.afterSlot.foreach { y =>
        RemodelAfterSlot.create(key, y.id, y.slotitemId, y.locked, y.level, now)
      }
      key
    }
  }

  def createOrig(
      memberId: Long,
      flag: Boolean,
      beforeItemId: Int,
      afterItemId: Int,
      vocieId: Int,
      useSlotIds: String,
      certain: Boolean,
      beforeItemLevel: Int,
      firstShipId: Int,
      secondShipId: Option[Int] = None,
      created: Long = System.currentTimeMillis())(implicit session: DBSession = autoSession): Long = {
    val generatedKey = withSQL {
      insert.into(Remodel).columns(
        column.memberId,
        column.flag,
        column.beforeItemId,
        column.afterItemId,
        column.vocieId,
        column.useSlotIds,
        column.certain,
        column.beforeItemLevel,
        column.firstShipId,
        column.secondShipId,
        column.created
      ).values(
          memberId,
          flag,
          beforeItemId,
          afterItemId,
          vocieId,
          useSlotIds,
          certain,
          beforeItemLevel,
          firstShipId,
          secondShipId,
          created
        )
    }.updateAndReturnGeneratedKey().apply()

    generatedKey
  }

  def save(entity: Remodel)(implicit session: DBSession = autoSession): Remodel = {
    withSQL {
      update(Remodel).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.flag -> entity.flag,
        column.beforeItemId -> entity.beforeItemId,
        column.afterItemId -> entity.afterItemId,
        column.vocieId -> entity.vocieId,
        column.useSlotIds -> entity.useSlotIds,
        column.certain -> entity.certain,
        column.beforeItemLevel -> entity.beforeItemLevel,
        column.firstShipId -> entity.firstShipId,
        column.secondShipId -> entity.secondShipId,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: Remodel)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(Remodel).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
