package ranking.common

import models.join.ShipWithName

import scala.concurrent.duration.Duration
import scala.xml.Elem

/**
 * @author ponkotuy
 * Date: 15/01/29.
 */
trait Ranking {
  // Titleとして使用
  def title: String

  // Ranking一覧で、Ranking毎のdivのclass設定に使用
  def divClass: String

  // 注釈。同じコメントは1つに纏められます
  def comment: Seq[String]

  // Rankingを生成するのに使用
  def rankingQuery(limit: Int): Seq[RankingElement]
}

object Ranking {
  val colmd3 = "col-md-3 col-sm-4"
  val collg3 = "col-lg-3 col-md-4 col-sm-4"
  val comment7days = "7日以内にデータ更新のあった提督に限ります"
  val comment30days = "図鑑に限り30日以内に図鑑を開いてデータ更新した提督に限ります"

  def values = RankingType.values.flatMap(_.rankings)

  def fromString(str: String): Option[Ranking] = values.find(_.toString == str)

  def agoMillis(d: Duration): Long = System.currentTimeMillis() - d.toMillis
  def toElem(ship: ShipWithName): Elem = <span>{ship.name}<small>{"Lv" + ship.lv}</small></span>
}
