package models

import scalikejdbc.SQLInterpolation._
import scalikejdbc.{DBSession, WrappedResultSet}
import com.ponkotuy.data

/**
 *
 * @author ponkotuy
 * Date: 2014/03/04.
 */
case class DeckPort(id: Int, memberId: Long, missionId: Long, name: String, created: Long)

object DeckPort extends SQLSyntaxSupport[DeckPort] {
  def apply(x: SyntaxProvider[DeckPort])(rs: WrappedResultSet): DeckPort = apply(x.resultName)(rs)
  def apply(x: ResultName[DeckPort])(rs: WrappedResultSet): DeckPort = new DeckPort(
    rs.int(x.id),
    rs.long(x.memberId),
    rs.long(x.missionId),
    rs.string(x.name),
    rs.long(x.created)
  )

  def create(dp: data.DeckPort)(
      implicit session: DBSession = DeckPort.autoSession): Unit = {
    val created = System.currentTimeMillis()
    val mission = dp.mission.map(Mission.create(_, created))
    val missionId = mission.map(_.id).getOrElse(-1L)
    DeckShip.bulkCreate(dp.id, dp.memberId, dp.ships)
    withSQL {
      insert.into(DeckPort).namedValues(
        column.id -> dp.id, column.memberId -> dp.memberId, column.missionId -> missionId,
        column.name -> dp.name, column.created -> created
      )
    }
  }
}

case class Mission(id: Long, page: Int, number: Int, completeTime: Long, created: Long)

object Mission extends SQLSyntaxSupport[Mission] {
  def apply(x: SyntaxProvider[Mission])(rs: WrappedResultSet): Mission = apply(x.resultName)(rs)
  def apply(x: ResultName[Mission])(rs: WrappedResultSet): Mission = new Mission(
    rs.long(x.id),
    rs.int(x.page),
    rs.int(x.number),
    rs.long(x.completeTime),
    rs.long(x.created)
  )

  def create(m: data.Mission, created: Long = System.currentTimeMillis())(
      implicit session: DBSession = Mission.autoSession): Mission = {
    val id = withSQL {
      insert.into(Mission).namedValues(
        column.page -> m.page, column.number -> m.number, column.completeTime -> m.completeTime,
        column.created -> created
      )
    }.updateAndReturnGeneratedKey().apply()
    Mission(id, m.page, m.number, m.completeTime, created)
  }
}

case class DeckShip(id: Long, deckId: Int, memberId: Long, shipId: Int)

object DeckShip extends SQLSyntaxSupport[DeckShip] {
  def apply(x: SyntaxProvider[DeckShip])(rs: WrappedResultSet): DeckShip = apply(x.resultName)(rs)
  def apply(x: ResultName[DeckShip])(rs: WrappedResultSet): DeckShip = new DeckShip(
    rs.long(x.id),
    rs.int(x.deckId),
    rs.long(x.memberId),
    rs.int(x.shipId)
  )

  def create(deckId: Int, memberId: Long, shipId: Int)(
      implicit session: DBSession = DeckShip.autoSession): DeckShip = {
    val id = withSQL {
      insert.into(DeckShip).namedValues(
        column.deckId -> deckId, column.memberId -> memberId, column.shipId -> shipId
      )
    }.updateAndReturnGeneratedKey().apply()
    DeckShip(id, deckId, memberId, shipId)
  }

  def bulkCreate(deckId: Int, memberId: Long, ships: List[Int])(
      implicit session: DBSession = DeckShip.autoSession): Unit = {
    require(ships.nonEmpty)
    applyUpdate {
      insert.into(DeckShip)
        .columns(column.deckId, column.memberId, column.shipId)
        .values(ships.map(Seq(deckId, memberId, _)))
    }
  }
}
