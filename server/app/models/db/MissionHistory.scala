package models.db

import models.other.MissionWithDeckId
import scalikejdbc._
import util.scalikejdbc.BulkInsert._

case class MissionHistory(
  id: Long,
  memberId: Long,
  deckId: Int,
  page: Int,
  number: Int,
  completeTime: Long,
  created: Long) {

  def save()(implicit session: DBSession = MissionHistory.autoSession): MissionHistory = MissionHistory.save(this)(session)

  def destroy()(implicit session: DBSession = MissionHistory.autoSession): Unit = MissionHistory.destroy(this)(session)

}


object MissionHistory extends SQLSyntaxSupport[MissionHistory] {

  override val tableName = "mission_history"

  override val columns = Seq("id", "member_id", "deck_id", "page", "number", "complete_time", "created")

  def apply(mh: SyntaxProvider[MissionHistory])(rs: WrappedResultSet): MissionHistory = apply(mh.resultName)(rs)
  def apply(mh: ResultName[MissionHistory])(rs: WrappedResultSet): MissionHistory = new MissionHistory(
    id = rs.get(mh.id),
    memberId = rs.get(mh.memberId),
    deckId = rs.get(mh.deckId),
    page = rs.get(mh.page),
    number = rs.get(mh.number),
    completeTime = rs.get(mh.completeTime),
    created = rs.get(mh.created)
  )

  val mh = MissionHistory.syntax("mh")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[MissionHistory] = {
    withSQL {
      select.from(MissionHistory as mh).where.eq(mh.id, id)
    }.map(MissionHistory(mh.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[MissionHistory] = {
    withSQL(select.from(MissionHistory as mh)).map(MissionHistory(mh.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(MissionHistory as mh)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[MissionHistory] = {
    withSQL {
      select.from(MissionHistory as mh).where.append(sqls"${where}")
    }.map(MissionHistory(mh.resultName)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(MissionHistory as mh).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def create(
    memberId: Long,
    deckId: Int,
    page: Int,
    number: Int,
    completeTime: Long,
    created: Long)(implicit session: DBSession = autoSession): MissionHistory = {
    val generatedKey = withSQL {
      insert.into(MissionHistory).columns(
        column.memberId,
        column.deckId,
        column.page,
        column.number,
        column.completeTime,
        column.created
      ).values(
          memberId,
          deckId,
          page,
          number,
          completeTime,
          created
        )
    }.updateAndReturnGeneratedKey().apply()

    MissionHistory(
      id = generatedKey,
      memberId = memberId,
      deckId = deckId,
      page = page,
      number = number,
      completeTime = completeTime,
      created = created)
  }

  def save(entity: MissionHistory)(implicit session: DBSession = autoSession): MissionHistory = {
    withSQL {
      update(MissionHistory).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.deckId -> entity.deckId,
        column.page -> entity.page,
        column.number -> entity.number,
        column.completeTime -> entity.completeTime,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def bulkInsert(ms: Seq[MissionWithDeckId], memberId: Long, created: Long = System.currentTimeMillis())(
      implicit session: DBSession = autoSession): Unit = {
    val exists = findAllBy(sqls"mh.member_id = ${memberId} and mh.complete_time in (${ms.map(_.completeTime)})").map(_.completeTime)
    val ms_ = ms.filterNot { m => exists.contains(m.completeTime) }
    if(ms_.isEmpty) return
    applyUpdate {
      insert.into(MissionHistory).columns(
        column.memberId, column.deckId, column.page, column.number, column.completeTime, column.created
      ).multiValues(
          Seq.fill(ms_.size)(memberId),
          ms_.map(_.deckId),
          ms_.map(_.page),
          ms_.map(_.number),
          ms_.map(_.completeTime),
          Seq.fill(ms_.size)(created)
        )
    }
  }

  def destroy(entity: MissionHistory)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(MissionHistory).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
