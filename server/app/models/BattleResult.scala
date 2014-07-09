package models

import scala.util.Try
import scalikejdbc._
import sqls.distinct
import com.ponkotuy.data
import dat.{CellWithRank, BattleResultWithCell, Stage, ShipDrop}

case class BattleResult(
  id: Long,
  memberId: Long,
  areaId: Int,
  infoNo: Int,
  cell: Int,
  enemies: String,
  winRank: String,
  questName: String,
  questLevel: Int,
  enemyDeck: String,
  firstClear: Boolean,
  getShipId: Option[Int] = None,
  getShipType: Option[String] = None,
  getShipName: Option[String] = None,
  created: Long) {

  def save()(implicit session: DBSession = BattleResult.autoSession): BattleResult = BattleResult.save(this)(session)

  def destroy()(implicit session: DBSession = BattleResult.autoSession): Unit = BattleResult.destroy(this)(session)

}


object BattleResult extends SQLSyntaxSupport[BattleResult] {

  override val tableName = "battle_result"

  override val columns = Seq("id", "member_id", "area_id", "info_no", "cell", "enemies", "win_rank", "quest_name", "quest_level", "enemy_deck", "first_clear", "get_ship_id", "get_ship_type", "get_ship_name", "created")

  def apply(br: ResultName[BattleResult])(rs: WrappedResultSet): BattleResult = new BattleResult(
    id = rs.long(br.id),
    memberId = rs.long(br.memberId),
    areaId = rs.int(br.areaId),
    infoNo = rs.int(br.infoNo),
    cell = rs.int(br.cell),
    enemies = rs.string(br.enemies),
    winRank = rs.string(br.winRank),
    questName = rs.string(br.questName),
    questLevel = rs.int(br.questLevel),
    enemyDeck = rs.string(br.enemyDeck),
    firstClear = rs.boolean(br.firstClear),
    getShipId = rs.intOpt(br.getShipId),
    getShipType = rs.stringOpt(br.getShipType),
    getShipName = rs.stringOpt(br.getShipName),
    created = rs.long(br.created)
  )

  val br = BattleResult.syntax("br")
  val ci = CellInfo.syntax("ci")
  val ms = MasterShipBase.syntax("ms")

  override val autoSession = AutoSession

  def find(id: Long)(implicit session: DBSession = autoSession): Option[BattleResult] = {
    withSQL {
      select.from(BattleResult as br).where.eq(br.id, id)
    }.map(BattleResult(br.resultName)).single().apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[BattleResult] = {
    withSQL(select.from(BattleResult as br)).map(BattleResult(br.resultName)).list().apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls"count(1)").from(BattleResult as br)).map(rs => rs.long(1)).single().apply().get
  }

  def findAllBy(where: SQLSyntax, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[BattleResult] = {
    withSQL {
      select.from(BattleResult as br).where.append(sqls"${where}")
        .limit(limit).offset(offset)
    }.map(BattleResult(br.resultName)).list().apply()
  }

  def findAllByWithCell(where: SQLSyntax, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[BattleResultWithCell] = {
    withSQL {
      select.from(BattleResult as br)
        .leftJoin(CellInfo as ci)
          .on(sqls"${br.areaId} = ${ci.areaId} and ${br.infoNo} = ${ci.infoNo} and ${br.cell} = ${ci.cell}")
        .where.append(sqls"${where}")
        .orderBy(br.created).desc
        .limit(limit).offset(offset)
    }.map(BattleResultWithCell(br, ci)).list().apply()
  }

  def findAllShipByNameLike(q: String)(implicit session: DBSession = autoSession): List[MasterShipBase] = {
    withSQL {
      select(distinct(ms.resultAll)).from(BattleResult as br)
        .innerJoin(MasterShipBase as ms).on(br.getShipId, ms.id)
        .where.like(ms.name, q)
    }.map(MasterShipBase(ms)).list().apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(BattleResult as br).where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def countByWithCellInfo(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls"count(1)").from(BattleResult as br)
        .leftJoin(CellInfo as ci)
          .on(sqls"${br.areaId} = ${ci.areaId} and ${br.infoNo} = ${ci.infoNo} and ${br.cell} = ${ci.cell}")
        .where.append(sqls"${where}")
    }.map(_.long(1)).single().apply().get
  }

  def countAllGroupByDrop(area: Int, info: Int, rank: String)(
      implicit session: DBSession = autoSession): List[(ShipDrop, Long)] = {
    withSQL {
      select(br.*, sqls"count(1) as cnt")
        .from(BattleResult as br)
        .where.eq(br.areaId, area).and.eq(br.infoNo, info).and.in(br.winRank, rank.map(_.toString))
        .groupBy(br.areaId, br.infoNo, br.cell, br.getShipId)
        .orderBy(br.cell, sqls"cnt")
    }.map { rs =>
      ShipDrop(br)(rs) -> rs.long("cnt")
    }.list().apply()
  }

  def countCellsGroupByDrop(area: Int, info: Int, cell: Int, rank: String)(
      implicit session: DBSession = autoSession): List[(ShipDrop, Long)] = {
    withSQL {
      select(br.*, sqls"count(1) as cnt")
        .from(BattleResult as br)
        .where.eq(br.areaId, area).and.eq(br.infoNo, info).and.eq(br.cell, cell)
        .and.in(br.winRank, rank.map(_.toString))
        .groupBy(br.areaId, br.infoNo, br.cell, br.getShipId)
        .orderBy(br.cell, sqls"cnt")
    }.map { rs =>
      ShipDrop(br)(rs) -> rs.long("cnt")
    }.list().apply()
  }

  def countCellsAlphaGroupByDrop(area: Int, info: Int, alpha: String, rank: String)(
    implicit session: DBSession = autoSession): List[(ShipDrop, Long)] = {
    withSQL {
      select(br.*, sqls"count(1) as cnt")
        .from(BattleResult as br)
        .innerJoin(CellInfo as ci)
        .on(sqls"${br.areaId} = ${ci.areaId} and ${br.infoNo} = ${ci.infoNo} and ${br.cell} = ${ci.cell}")
        .where.eq(br.areaId, area).and.eq(br.infoNo, info).and.eq(ci.alphabet, alpha)
        .and.in(br.winRank, rank.map(_.toString))
        .groupBy(br.areaId, br.infoNo, ci.alphabet, br.getShipId)
        .orderBy(br.cell, sqls"cnt")
    }.map { rs =>
      ShipDrop(br)(rs) -> rs.long("cnt")
    }.list().apply()
  }

  def countAllByStage()(implicit session: DBSession = autoSession): List[(Stage, Long)] = {
    withSQL {
      select(br.*, sqls"count(1) as cnt")
        .from(BattleResult as br)
        .groupBy(br.areaId, br.infoNo)
        .orderBy(br.areaId, br.infoNo)
    }.map { rs =>
      Stage(br)(rs) -> rs.long("cnt")
    }.list().apply()
  }

  def countAllGroupByCells(where: SQLSyntax = sqls"1")(implicit session: DBSession = autoSession): List[(CellWithRank, Long)] = {
    withSQL {
      select(br.*, sqls"count(1) as cnt")
        .from(BattleResult as br)
        .where.append(sqls"${where}")
        .groupBy(br.areaId, br.infoNo, br.cell, br.winRank)
        .orderBy(br.areaId, br.infoNo, br.cell, br.winRank)
    }.map { rs =>
      CellWithRank(br)(rs) -> rs.long("cnt")
    }.list().apply()
  }

  def dropedCells(area: Int, info: Int)(implicit session: DBSession = autoSession): List[CellInfo] = {
    withSQL {
      select(br.areaId, br.infoNo, br.cell, ci.resultAll).from(BattleResult as br)
        .leftJoin(CellInfo as ci)
        .on(sqls"${br.areaId} = ${ci.areaId} and ${br.infoNo} = ${ci.infoNo} and ${br.cell} = ${ci.cell}")
        .where.eq(br.areaId, area).and.eq(br.infoNo, info)
        .groupBy(br.cell)
    }
  }.map { rs =>
    Try(CellInfo(ci)(rs)).getOrElse {
      val areaId = rs.int(1)
      val infoNo = rs.int(2)
      val cell = rs.int(3)
      CellInfo.noAlphabet(areaId, infoNo, cell)
    }
  }.list().apply()

  def dropedCellsAlpha(area: Int, info: Int)(implicit session: DBSession = autoSession): List[CellInfo] = {
    withSQL {
      select(br.areaId, br.infoNo, ci.resultAll).from(BattleResult as br)
        .innerJoin(CellInfo as ci)
        .on(sqls"${br.areaId} = ${ci.areaId} and ${br.infoNo} = ${ci.infoNo} and ${br.cell} = ${ci.cell}")
        .where.eq(br.areaId, area).and.eq(br.infoNo, info)
        .groupBy(ci.alphabet)
    }.map(CellInfo(ci)).list().apply()
  }

  def create(result: data.BattleResult, map: data.MapStart, memberId: Long)(
      implicit session: DBSession = autoSession): BattleResult = {
    val created = System.currentTimeMillis()
    createOrig(
      memberId, map.mapAreaId, map.mapInfoNo, map.no,
      result.enemies.mkString(","), result.winRank, result.questName, result.questLevel, result.enemyDeck,
      result.firstClear, result.getShip.map(_.id), result.getShip.map(_.stype), result.getShip.map(_.name), created)
  }

  def createOrig(
    memberId: Long,
    areaId: Int,
    infoNo: Int,
    cell: Int,
    enemies: String,
    winRank: String,
    questName: String,
    questLevel: Int,
    enemyDeck: String,
    firstClear: Boolean,
    getShipId: Option[Int] = None,
    getShipType: Option[String] = None,
    getShipName: Option[String] = None,
    created: Long)(implicit session: DBSession = autoSession): BattleResult = {
    val generatedKey = withSQL {
      insert.into(BattleResult).columns(
        column.memberId,
        column.areaId,
        column.infoNo,
        column.cell,
        column.enemies,
        column.winRank,
        column.questName,
        column.questLevel,
        column.enemyDeck,
        column.firstClear,
        column.getShipId,
        column.getShipType,
        column.getShipName,
        column.created
      ).values(
          memberId,
          areaId,
          infoNo,
          cell,
          enemies,
          winRank,
          questName,
          questLevel,
          enemyDeck,
          firstClear,
          getShipId,
          getShipType,
          getShipName,
          created
        )
    }.updateAndReturnGeneratedKey().apply()

    BattleResult(
      id = generatedKey,
      memberId = memberId,
      areaId = areaId,
      infoNo = infoNo,
      cell = cell,
      enemies = enemies,
      winRank = winRank,
      questName = questName,
      questLevel = questLevel,
      enemyDeck = enemyDeck,
      firstClear = firstClear,
      getShipId = getShipId,
      getShipType = getShipType,
      getShipName = getShipName,
      created = created)
  }

  def save(entity: BattleResult)(implicit session: DBSession = autoSession): BattleResult = {
    withSQL {
      update(BattleResult).set(
        column.id -> entity.id,
        column.memberId -> entity.memberId,
        column.areaId -> entity.areaId,
        column.infoNo -> entity.infoNo,
        column.cell -> entity.cell,
        column.enemies -> entity.enemies,
        column.winRank -> entity.winRank,
        column.questName -> entity.questName,
        column.questLevel -> entity.questLevel,
        column.enemyDeck -> entity.enemyDeck,
        column.firstClear -> entity.firstClear,
        column.getShipId -> entity.getShipId,
        column.getShipType -> entity.getShipType,
        column.getShipName -> entity.getShipName,
        column.created -> entity.created
      ).where.eq(column.id, entity.id)
    }.update().apply()
    entity
  }

  def destroy(entity: BattleResult)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
      delete.from(BattleResult).where.eq(column.id, entity.id)
    }.update().apply()
  }

}
