package models.query

import models.db.{DeckShipSnapshot, DeckSnapshot, MasterShipBase}
import models.join.DeckSnapshotWithAdmiral
import scalikejdbc._

/**
 *
 * @author ponkotuy
 * Date: 14/11/20.
 */
case class SnapshotSearch(q: String, snaps: Seq[DeckSnapshotWithAdmiral], count: Long, page: Int) {
  import models.query.SnapshotSearch._
  def enable: Boolean = snaps.nonEmpty
  def orElse(f: => SnapshotSearch) = if(enable) this else f
  def maxPage: Int = ((count + PageCount - 1)/PageCount - 1).toInt
  def isMaxPage: Boolean = page == maxPage || page == 9
}

object SnapshotSearch {
  val PageCount = 10

  def empty(q: String) = SnapshotSearch(q, Nil, 0, 0)

  def search(q: String, page: Int): SnapshotSearch = {
    if(q.isEmpty) empty(q)
    else {
      val like = escapeLike(q)
      fromShipName(like, page) orElse fromTitle(like, page) orElse fromComment(like, page)
    }
  }

  private def fromShipName(q: String, page: Int): SnapshotSearch = {
    val shipIds = MasterShipBase.findAllByLike(s"$q%").map(_.id)
    if(shipIds.nonEmpty) {
      val deckIds = DeckShipSnapshot.findAllBy(sqls"dss.ship_id in (${shipIds})").map(_.deckId)
      if(deckIds.nonEmpty) {
        val where = sqls"ds.id in (${deckIds})"
        val count = DeckSnapshot.countBy(where)
        val snaps = if(count > 0) DeckSnapshot.findAllByWithAdmiral(where, PageCount, page*PageCount) else Nil
        SnapshotSearch(q, snaps, count, page)
      } else empty(q)
    } else empty(q)
  }

  private def fromTitle(q: String, page: Int) = {
    val where = sqls"ds.title like ${s"%$q%"}"
    val count = DeckSnapshot.countBy(where)
    val snaps = if(count > 0) DeckSnapshot.findAllByWithAdmiral(where, PageCount, page*PageCount) else Nil
    SnapshotSearch(q, snaps, count, page)
  }

  private def fromComment(q: String, page: Int) = {
    val where = sqls"ds.comment like ${s"%$q%"}"
    val count = DeckSnapshot.countBy(where)
    val snaps = if(count > 0) DeckSnapshot.findAllByWithAdmiral(where, PageCount, page*PageCount) else Nil
    SnapshotSearch(q, snaps, count, page)
  }

  def escapeLike(q: String) = q.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")
}
