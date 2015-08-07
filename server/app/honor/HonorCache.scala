package honor

import models.db
import models.db._
import scalikejdbc._

class HonorCache(memberId: Long) {
  lazy val shipBook = ShipBook.findAllBy(sqls.eq(ShipBook.sb.memberId, memberId))
  lazy val mapInfo = MapInfo.findAllBy(sqls.eq(MapInfo.column.memberId, memberId))
  lazy val shipWithName = Ship.findAllByUserWithName(memberId)
  lazy val luckyMaxShip = Ship.findAllWithSpec(sqls.eq(Ship.s.memberId, memberId).and.eq(Ship.mss.luckyMax, Ship.s.lucky))
  lazy val basic = Basic.findByUser(memberId)
  lazy val material = db.Material.findByUser(memberId)
  lazy val yomeShip = YomeShip.findAllByWithName(sqls.eq(db.YomeShip.ys.memberId, memberId))
}
