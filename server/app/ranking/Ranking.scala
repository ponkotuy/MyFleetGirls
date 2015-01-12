package ranking

import models.join.ShipWithName
import util.ehcache.TimeToLiveCache

import scala.concurrent.duration._
import scala.xml.Elem

/**
 *
 * @author ponkotuy
 * Date: 14/10/09.
 */
trait Ranking {
  def title: String // Titleとして使用
  def divClass: String // Ranking一覧で、Ranking毎のdivのclass設定に使用
  def comment: List[String] // 注釈。同じコメントは1つに纏められます
  def rankingQuery(limit: Int): List[RankingElement] // Rankingを生成するのに使用
}

case class CachedRanking(ranking: Ranking) extends Ranking {
  override def title: String = ranking.title
  override def comment: List[String] = ranking.comment
  override def divClass: String = ranking.divClass

  override def rankingQuery(limit: Int): List[RankingElement] = {
    if(limit <= Cache.getSize) {
      val result = (0 until limit).flatMap(Cache.get).toList
      if(result.size == limit) result else updateAndReturn(limit)
    } else updateAndReturn(limit)
  }
  private[this] def updateAndReturn(limit: Int): List[RankingElement] = {
    val result = ranking.rankingQuery(limit)
    val map = result.zipWithIndex.map { case (elem, i) => i -> elem }.toMap
    Cache.putAll(map)
    result
  }

  private object Cache extends TimeToLiveCache[Int, RankingElement] {
    override def cacheName = ranking.getClass.getName
    override def liveSeconds = 60.minutes.toSeconds
    override def maxEntries = 10000
    override def default(k: Int): Option[RankingElement] = None
  }
}

object Ranking {
  val colmd3 = "col-md-3 col-sm-4"
  val collg3 = "col-lg-3 col-md-4 col-sm-4"
  val comment7days = "7日以内にデータ更新のあった提督に限ります"
  val comment30days = "図鑑に限り30日以内に図鑑を開いてデータ更新した提督に限ります"

  val values: Array[Ranking] = Array(
    CachedRanking(MaterialRanking),
    CachedRanking(FirstShipRanking),
    CachedRanking(LuckRanking),
    CachedRanking(ShipBookRanking),
    CachedRanking(MarriedRanking),
    CachedRanking(SumShipExpRanking),
    CachedRanking(ExpByShipRanking),
    CachedRanking(ItemBookRanking),
    CachedRanking(FirstShipRate)
  )

  def fromString(str: String): Option[Ranking] = values.find(_.toString == str)

  def agoMillis(d: Duration): Long = System.currentTimeMillis() - d.toMillis
  def toElem(ship: ShipWithName): Elem = <span>{ship.name}<small>{"Lv" + ship.lv}</small></span>
}

@SerialVersionUID(1L)
case class RankingElement(name: String, value: Elem, url: Option[String])

object RankingElement {
  def apply(name: String, value: Elem): RankingElement = RankingElement(name, value, None)
  def apply(name: String, value: Elem, url: String): RankingElement = RankingElement(name, value, Some(url))
}
