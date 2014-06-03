package models

import scalikejdbc._

case class ShipBattleResult(
  battleId: Long,
  memberId: Long,
  id: Byte,
  exp: Int,
  lostFlag: Boolean) {

  def save()(implicit session: DBSession = ShipBattleResult.autoSession): ShipBattleResult = ShipBattleResult.save(this)(session)

  def destroy()(implicit session: DBSession = ShipBattleResult.autoSession): Unit = ShipBattleResult.destroy(this)(session)

}


object ShipBattleResult extends SQLSyntaxSupport[ShipBattleResult] {

  override val tableName = "ship_battle_result"

  override val columns = Seq("battle_id", "member_id", "id", "exp", "lost_flag")

  def apply(sbr: ResultName[ShipBattleResult])(rs: WrappedResultSet): ShipBattleResult = new ShipBattleResult(
    battleId = rs.long(sbr.battleId),
    memberId = rs.long(sbr.memberId),
    id = rs.byte(sbr.id),
    exp = rs.int(sbr.exp),
    lostFlag = rs.boolean(sbr.lostFlag)
  )

  val sbr = ShipBattleResult.syntax("sbr")

  override val autoSession = AutoSession

  def find(battleId: Long, memberId: Long, id: Byte, exp: Int, lostFlag: Boolean)(implicit session: DBSession = autoSession): Option[ShipBattleResult] = {
    withSQL {
      select.from(ShipBattleResult as sbr).where.eq(sbr.battleId, battleId).and.eq(sbr.memberId, memberId).and.eq(sbr.id, id).and.eq(sbr.exp, exp).and.eq(sbr.lostFlag, lostFlag)
    }.map(ShipBattleResult(sbr.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[ShipBattleResult] = {
    withSQL(select.from(ShipBattleResult as sbr)).map(ShipBattleResult(sbr.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(ShipBattleResult as sbr)).map(rs => rs.long(1)).single.apply().get
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[ShipBattleResult] = {
    withSQL {
      select.from(ShipBattleResult as sbr).where.append(sqls"${where}")
    }.map(ShipBattleResult(sbr.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(ShipBattleResult as sbr).where.append(sqls"${where}")
    }.map(_.long(1)).single.apply().get
  }

  def create(
    battleId: Long,
    memberId: Long,
    id: Byte,
    exp: Int,
    lostFlag: Boolean)(implicit session: DBSession = autoSession): ShipBattleResult = {
    withSQL {
      insert.into(ShipBattleResult).columns(
        column.battleId,
        column.memberId,
        column.id,
        column.exp,
        column.lostFlag
      ).values(
        battleId,
        memberId,
        id,
        exp,
        lostFlag
      )
    }.update.apply()

    ShipBattleResult(
      battleId = battleId,
      memberId = memberId,
      id = id,
      exp = exp,
      lostFlag = lostFlag)
  }

  def save(entity: ShipBattleResult)(implicit session: DBSession = autoSession): ShipBattleResult = {
    withSQL {
      update(ShipBattleResult).set(
        column.battleId -> entity.battleId,
        column.memberId -> entity.memberId,
        column.id -> entity.id,
        column.exp -> entity.exp,
        column.lostFlag -> entity.lostFlag
      ).where.eq(column.battleId, entity.battleId).and.eq(column.memberId, entity.memberId).and.eq(column.id, entity.id).and.eq(column.exp, entity.exp).and.eq(column.lostFlag, entity.lostFlag)
    }.update.apply()
    entity
  }

  def destroy(entity: ShipBattleResult)(implicit session: DBSession = autoSession): Unit = {
    withSQL { delete.from(ShipBattleResult).where.eq(column.battleId, entity.battleId).and.eq(column.memberId, entity.memberId).and.eq(column.id, entity.id).and.eq(column.exp, entity.exp).and.eq(column.lostFlag, entity.lostFlag) }.update.apply()
  }

}
