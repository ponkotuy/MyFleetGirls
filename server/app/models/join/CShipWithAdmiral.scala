package models.join

import models.db._
import scalikejdbc._

/**
 * Date: 14/07/15.
 */
case class CShipWithAdmiral(createShip: CreateShip, admiral: Admiral, masterShip: MasterShipBase) extends Activity {
  def shipId: Int = createShip.resultShip
  def shipName: String = masterShip.name

  override def title: String = "艦娘建造"
  override def message: String = s"${nickname}提督が${shipName}を建造しました"
  override def url: String = controllers.routes.UserView.create(memberId).url
  override def created: Long = createShip.created
  override def id: String = s"${createShip.memberId}_${createShip.kDock}_${createShip.completeTime}"
}

object CShipWithAdmiral {
  def apply(cs: SyntaxProvider[CreateShip], a: SyntaxProvider[Admiral], ms: SyntaxProvider[MasterShipBase])(
      rs: WrappedResultSet): CShipWithAdmiral =
    CShipWithAdmiral(CreateShip(cs)(rs), Admiral(a)(rs), MasterShipBase(ms)(rs))
}
