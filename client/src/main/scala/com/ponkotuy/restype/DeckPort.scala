package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object DeckPort extends ResType {
  import ResType._

  val FleetMax = 4
  // 艦隊情報がRoute等で必要なので溜めておく
  private[restype] var fleets: Seq[List[Int]] = Nil
  private[restype] var firstFleet: List[Int] = Nil

  override def regexp: Regex = s"\\A$GetMember/deck_port\\z".r

  override def postables(q: Query): Seq[Result] = {
    val decks = data.DeckPort.fromJson(q.obj)
    firstFleet = extractFleetShips(decks)(1)
    fleets = (1 to FleetMax).map(extractFleetShips(decks))
    if(decks.nonEmpty) {
      val message = decks.map(_.summary).mkString("\n")
      NormalPostable("/deckport", write(decks), 1, message) :: Nil
    } else Nil
  }


  private def extractFleetShips(decks: Iterable[data.DeckPort])(num: Int): List[Int] =
    decks.find(_.id == num).map(_.ships).getOrElse(Nil)}
