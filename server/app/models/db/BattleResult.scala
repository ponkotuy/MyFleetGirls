package models.db

import models.join._

import scala.util.Try
import scalikejdbc._
import sqls.distinct
import com.ponkotuy.data

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
    mapRank: Option[MapRank] = None,
    created: Long) {

  def save()(implicit session: DBSession = BattleResult.autoSession): BattleResult = BattleResult.save(this)(session)

  def destroy()(implicit session: DBSession = BattleResult.autoSession): Unit = BattleResult.destroy(this)(session)

}


object BattleResult extends SQLSyntaxSupport[BattleResult] {

  override val tableName = "battle_result"

  override val columns = Seq("id", "member_id", "area_id", "info_no", "cell", "enemies", "win_rank", "quest_name",
    "quest_level", "enemy_deck", "first_clear", "get_ship_id", "get_ship_type", "get_ship_name", "map_rank", "created")

  def apply(br: ResultName[BattleResult])(rs: WrappedResultSet): BattleResult = new BattleResult(
    id = rs.get(br.id),
    memberId = rs.get(br.memberId),
    areaId = rs.get(br.areaId),
    infoNo = rs.get(br.infoNo),
    cell = rs.get(br.cell),
    enemies = rs.get(br.enemies),
    winRank = rs.get(br.winRank),
    questName = rs.get(br.questName),
    questLevel = rs.get(br.questLevel),
    enemyDeck = rs.get(br.enemyDeck),
    firstClear = rs.get(br.firstClear),
    getShipId = rs.get(br.getShipId),
    getShipType = rs.get(br.getShipType),
    getShipName = rs.get(br.getShipName),
    mapRank = rs.get[Option[Int]](br.mapRank).flatMap(MapRank.fromInt),
    created = rs.get(br.created)
  )

  val br = BattleResult.syntax("br")
  val br2 = SubQuery.syntax("br2", br.resultName)
  val ci = CellInfo.syntax("ci")
  val ms = MasterShipBase.syntax("ms")
  val a = Admiral.syntax("a")

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

  def findAllByWithCell(where: SQLSyntax, limit: Int = Int.MaxValue, offset: Int = 0, orderBy: SQLSyntax = br.created.desc)(
      implicit session: DBSession = autoSession): List[BattleResultWithCell] = {
    if(orderBy == br.created.desc) findAllByWithCellOrderByCreated(where, limit, offset)
    else {
      withSQL {
        select.from(BattleResult as br)
            .leftJoin(CellInfo as ci)
              .on(sqls.eq(br.areaId, ci.areaId).and.eq(br.infoNo, ci.infoNo).and.eq(br.cell, ci.cell))
            .where(where)
            .orderBy(orderBy)
            .limit(limit).offset(offset)
      }.map(BattleResultWithCell(br, ci)).list().apply()
    }
  }

  /**
   * MariaDBがアホでcreatedのindexを使ってしまうので、SubQuery化した方がindexを2種類使えて早い事案
   * それでも遅いので日付条件を絞り込むの推奨
   */
  def findAllByWithCellOrderByCreated(where: SQLSyntax, limit: Int = Int.MaxValue, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[BattleResultWithCell] = {
    withSQL {
      select.from(BattleResult as br)
          .leftJoin(CellInfo as ci)
            .on(sqls.eq(br.areaId, ci.areaId).and.eq(br.infoNo, ci.infoNo).and.eq(br.cell, ci.cell))
          .where.in(br.id, select(br.id).from(BattleResult as br).where(where))
          .orderBy(br.created.desc)
          .limit(limit).offset(offset)
    }
  }.map(BattleResultWithCell(br, ci)).list().apply()

  def findAllShipByNameLike(q: String)(implicit session: DBSession = autoSession): List[MasterShipBase] = {
    withSQL {
      select(distinct(ms.resultAll)).from(BattleResult as br)
        .innerJoin(MasterShipBase as ms).on(br.getShipId, ms.id)
        .where.like(ms.name, q)
    }.map(MasterShipBase(ms)).list().apply()
  }

  def findWithUserBy(where: SQLSyntax, limit: Int = 10, offset: Int = 0)(
      implicit session: DBSession = autoSession): List[BattleResultWithUser] = {
    withSQL {
      select.from(BattleResult as br)
        .innerJoin(Admiral as a).on(br.memberId, a.id)
        .where(where)
        .orderBy(br.created).desc
        .limit(limit).offset(offset)
    }.map(BattleResultWithUser(br, a)).list().apply()
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

  def countCellsGroupByDrop(area: Int, info: Int, cell: Int, rank: String, where: SQLSyntax = sqls"true")(
      implicit session: DBSession = autoSession): List[(ShipDrop, Long)] = {
    withSQL {
      select(br.*, sqls"count(1) as cnt")
        .from(BattleResult as br)
        .where.eq(br.areaId, area).and.eq(br.infoNo, info).and.eq(br.cell, cell)
        .and.in(br.winRank, rank.map(_.toString)).and.append(where)
        .groupBy(br.areaId, br.infoNo, br.cell, br.getShipId)
        .orderBy(br.cell, sqls"cnt")
    }.map { rs =>
      ShipDrop(br)(rs) -> rs.long("cnt")
    }.list().apply()
  }

  def countCellsAlphaGroupByDrop(area: Int, info: Int, alpha: String, rank: String, where: SQLSyntax = sqls"true")(
    implicit session: DBSession = autoSession): List[(ShipDrop, Long)] = {
    withSQL {
      select(br.*, sqls"count(1) as cnt")
        .from(BattleResult as br)
        .innerJoin(CellInfo as ci)
        .on(sqls"${br.areaId} = ${ci.areaId} and ${br.infoNo} = ${ci.infoNo} and ${br.cell} = ${ci.cell}")
        .where.eq(br.areaId, area).and.eq(br.infoNo, info).and.eq(ci.alphabet, alpha)
        .and.in(br.winRank, rank.map(_.toString)).and.append(where)
        .groupBy(br.areaId, br.infoNo, ci.alphabet, br.getShipId)
        .orderBy(br.cell, sqls"cnt")
    }.map { rs =>
      ShipDrop(br)(rs) -> rs.long("cnt")
    }.list().apply()
  }

  def countAllByStage()(implicit session: DBSession = autoSession): List[(Stage, Long)] = {
    withSQL {
      select(br.areaId, br.infoNo, sqls"count(1) as cnt")
        .from(BattleResult as br)
        .groupBy(br.areaId, br.infoNo)
        .orderBy(br.areaId, br.infoNo)
    }.map { rs =>
      Stage(br)(rs) -> rs.long("cnt")
    }.list().apply()
  }

  def countAllGroupByCells(where: SQLSyntax = sqls"1")(implicit session: DBSession = autoSession): List[(CellWithRank, Long)] = {
    withSQL {
      select(br2(br).areaId, br2(br).infoNo, br2(br).cell, br2(br).winRank, ci.alphabet, sqls"br2.cnt")
          .from(
            select(br.result.areaId, br.result.infoNo, br.result.cell, br.result.winRank, sqls"count(1) as cnt")
                .from(BattleResult as br).where.append(where)
                .groupBy(br.areaId, br.infoNo, br.cell, br.winRank) as br2
          )
          .leftJoin(CellInfo as ci).on(sqls.eq(br2(br).areaId, ci.areaId).and.eq(br2(br).infoNo, ci.infoNo).and.eq(br2(br).cell, ci.cell))
          .orderBy(br2(br).areaId, br2(br).infoNo, br2(br).cell, br2(br).winRank)
    }.map { rs =>
      val areaId = rs.int(br2(br).areaId)
      val infoNo = rs.int(br2(br).infoNo)
      val cell = rs.int(br2(br).cell)
      val rank = rs.string(br2(br).winRank)
      val alpha = rs.stringOpt(ci.alphabet)
      CellWithRank(areaId, infoNo, cell, rank, alpha) -> rs.long("cnt")
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

  def existsShip(shipId: Int)(implicit session: DBSession = autoSession): Boolean = withSQL {
    select(br.getShipId).from(BattleResult as br).where.eq(br.getShipId, shipId).limit(1)
  }.map(_ => true).single().apply().isDefined

  def create(result: data.BattleResult, map: data.MapStart, memberId: Long)(
      implicit session: DBSession = autoSession): Unit = {
    val created = System.currentTimeMillis()
    val mapinfo = MapInfo.find(map.mapAreaId*10 + map.mapInfoNo, memberId)
    createOrig(
      memberId,
      map.mapAreaId,
      map.mapInfoNo,
      map.no,
      result.enemies.mkString(","),
      result.winRank,
      result.questName,
      result.questLevel,
      result.enemyDeck,
      result.firstClear,
      result.getShip.map(_.id),
      result.getShip.map(_.stype),
      result.getShip.map(_.name),
      mapinfo.flatMap(_.rank),
      created)
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
      mapRank: Option[MapRank] = None,
      created: Long)(implicit session: DBSession = autoSession): Unit = {
    withSQL {
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
        column.mapRank,
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
          mapRank.map(_.v),
          created
        )
    }.updateAndReturnGeneratedKey().apply()
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
        column.mapRank -> entity.mapRank.map(_.v),
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
