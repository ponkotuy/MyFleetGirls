package honor

import models.db
import models.db._
import models.join.{ShipWithName, ShipWithSpecs}
import scalikejdbc._

class HonorCache(memberId: Long) {
  lazy val shipBook: List[ShipBook] = ShipBook.findAllBy(sqls.eq(ShipBook.sb.memberId, memberId))
  lazy val mapInfo: List[MapInfo] = MapInfo.findAllBy(sqls.eq(MapInfo.column.memberId, memberId))
  lazy val shipWithName: List[ShipWithName] = Ship.findAllByUserWithName(memberId)
  lazy val luckyMaxShip: List[ShipWithSpecs] =
    Ship.findAllWithSpec(sqls.eq(Ship.s.memberId, memberId).and.eq(Ship.mss.luckyMax, Ship.s.lucky))
  lazy val basic: Option[Basic] = Basic.findByUser(memberId)
  lazy val material: Option[Material] = db.Material.findByUser(memberId)
  lazy val yomeShip: List[ShipWithName] = YomeShip.findAllByWithName(sqls.eq(db.YomeShip.ys.memberId, memberId))
}
