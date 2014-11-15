package ranking

import models.join.ShipWithName

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

object Ranking {
  val colmd3 = "col-md-3 col-sm-4"
  val collg3 = "col-lg-3 col-md-4 col-sm-4"
  val comment7days = "7日以内にデータ更新のあった提督に限ります"
  val comment30days = "図鑑に限り30日以内に図鑑を開いてデータ更新した提督に限ります"

  val values: Array[Ranking] = Array(
    MaterialRanking,
    YomeRanking,
    FirstShipRanking,
    ShipBookRanking,
    MarriedRanking,
    SumShipExpRanking,
    ExpByShipRanking,
    ItemBookRanking
  )

  def fromString(str: String): Option[Ranking] = values.find(_.toString == str)

  def agoMillis(d: Duration): Long = System.currentTimeMillis() - d.toMillis
  def toElem(ship: ShipWithName): Elem = <span>{ship.name}<small>{"Lv" + ship.lv}</small></span>
}

case class RankingElement(name: String, value: Elem, url: Option[String])

object RankingElement {
  def apply(name: String, value: Elem): RankingElement = RankingElement(name, value, None)
  def apply(name: String, value: Elem, url: String): RankingElement = RankingElement(name, value, Some(url))
}
