package models.join

import models.db.{Admiral, Honor}
import scalikejdbc.{AutoSession, DBSession}

import scala.collection.breakOut

/**
 * Date: 15/03/18.
 * @author ponkotuy
 */
case class HonorWithAdmiral(category: Int, name: String, admirals: Seq[Admiral])

object HonorWithAdmiral {
  def fromRecords(rs: Seq[(Honor, Admiral)]): List[HonorWithAdmiral] = {
    val list: List[HonorWithAdmiral] = rs.groupBy { case (h, _) => (h.category, h.name) }
        .mapValues { xs => xs.map(_._2) }
        .map { case ((cat, name), a) => HonorWithAdmiral(cat, name, a) }(breakOut)
    list.sortBy(_.category)
  }
}

case class HonorWithRate(category: Int, name: String, admirals: Seq[Admiral], count: Long, rate: Double)

object HonorWithRate {
  def fromWithAdmiral(xs: Seq[HonorWithAdmiral])(implicit session: DBSession = AutoSession) = {
    val adCount = Admiral.countAll()
    val honorsCount = Honor.countName().withDefaultValue(0L)
    xs.map { x =>
      val count = honorsCount(x.name)
      HonorWithRate(x.category, x.name, x.admirals, count, count.toDouble / adCount)
    }
  }
}
