package honor

import models.db.Honor
import scalikejdbc._
import collection.breakOut

/**
 * @author ponkotuy
 * Date: 15/03/17.
 */
trait HonorCategory {
  def category: Int
  def approved(memberId: Long, db: HonorCache): List[String]
  def comment: String
}

object Honors {
  val values: Vector[HonorCategory] = Vector(ShipMaster, Yome, RankingTop, AllClear, ShipMaxLv, FleetAdmiral, Practice, ShipTypeBias, InitShip, Married, ManyShip, Rookie, FleetGroup, Lucky, SpecialDay, NotHave, HasUsers, Fetishism, Material)

  def fromUser(memberId: Long, set: Boolean = false): List[Honor] = {
    val where = sqls.toAndConditionOpt(
      Some(sqls.eq(Honor.column.memberId, memberId)),
      if(set) Some(sqls.eq(Honor.column.setBadge, true)) else None
    ).getOrElse(sqls"true")
    Honor.findAllBy(where)
  }

  def create(memberId: Long): Unit = {
    val cache = new HonorCache(memberId)
    val after: Map[Int, Seq[String]] = values.par.map { h =>
      h.category -> h.approved(memberId, cache)
    }(breakOut)
    val before: Set[String] = fromUser(memberId).map(_.name)(breakOut)
    after.foreach { case (cat, xs) =>
      xs.foreach { x =>
        if(!before.contains(x)) Honor.create(memberId, cat, x, false, false)
      }
    }
  }

  def category(cat: Int): Option[HonorCategory] = values.find(_.category == cat)

  def comment(cat: Int): Option[String] = category(cat).map(_.comment)

  def timer[T](name: String)(f: => T): T = {
    val start = System.nanoTime()
    val result = f
    println(s"${name}: ${(System.nanoTime() - start) * 0.000001} ms")
    result
  }
}
