package models.query

import models.db.{DeckSnapshot, SnapshotText}
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

  def search(q: String, page: Int): SnapshotSearch = {
    val ids = SnapshotText.search(q, limit = PageCount, offset = page*PageCount)
    val count = SnapshotText.searchCount(q)
    val snaps = DeckSnapshot.findAllByWithAdmiral(sqls.in(DeckSnapshot.ds.id, ids))
    SnapshotSearch(q, snaps, count, page)
  }
}
