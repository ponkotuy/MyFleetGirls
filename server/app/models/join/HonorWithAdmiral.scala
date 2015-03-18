package models.join

import models.db.{Admiral, Honor}

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
