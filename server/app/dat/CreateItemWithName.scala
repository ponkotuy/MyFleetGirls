package dat

import models._
import scalikejdbc._

/**
 * Date: 14/07/15.
 */
case class CreateItemWithName(
    slotitemId: Option[Int],
    fuel: Int,
    ammo: Int,
    steel: Int,
    bauxite: Int,
    shizaiFlag: Boolean,
    flagshipId: Int,
    created: Long,
    name: String,
    flagshipName: String)


object CreateItemWithName {
  def apply(ci: SyntaxProvider[CreateItem], mi: SyntaxProvider[MasterSlotItem], ms: SyntaxProvider[MasterShipBase])(
      rs: WrappedResultSet): CreateItemWithName =
    new CreateItemWithName(
      rs.intOpt(ci.slotitemId),
      rs.int(ci.fuel),
      rs.int(ci.ammo),
      rs.int(ci.steel),
      rs.int(ci.bauxite),
      rs.boolean(ci.shizaiFlag),
      rs.int(ci.flagship),
      rs.long(ci.created),
      rs.stringOpt(mi.name).getOrElse("失敗"),
      rs.string(ms.name)
    )
}

case class ItemMat(fuel: Int, ammo: Int, steel: Int, bauxite: Int, sType: Int, sTypeName: String)

object ItemMat {
  def apply(ci: SyntaxProvider[CreateItem], mst: SyntaxProvider[MasterStype])(rs: WrappedResultSet): ItemMat =
    new ItemMat(
      rs.int(ci.fuel),
      rs.int(ci.ammo),
      rs.int(ci.steel),
      rs.int(ci.bauxite),
      rs.int(mst.id),
      rs.string(mst.name)
    )
}

case class ItemWithAdmiral(createItem: CreateItem, admiral: Admiral, masterItem: MasterSlotItem) extends Activity {
  def itemName: String = masterItem.name

  override def title: String = "装備開発"
  override def message: String = s"${nickname}提督が${itemName}を開発しました"
  override def url: String = controllers.routes.UserView.create(memberId).url
  override def created: Long = createItem.created
}

object ItemWithAdmiral {
  def apply(ci: SyntaxProvider[CreateItem], a: SyntaxProvider[Admiral], mi: SyntaxProvider[MasterSlotItem])(
      rs: WrappedResultSet): ItemWithAdmiral =
    ItemWithAdmiral(CreateItem(ci.resultName)(rs), Admiral(a)(rs), MasterSlotItem(mi)(rs))
}
