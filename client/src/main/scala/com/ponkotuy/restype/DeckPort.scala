package com.ponkotuy.restype

import org.json4s._
import org.json4s.native.Serialization.write
import com.ponkotuy.data

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object DeckPort extends ResType {
  import ResType._

  val FleetMax = 4

  override def regexp: Regex = s"\\A$GetMember/deck\\z".r

  override def postables(req: Req, obj: JValue): Seq[Result] = {
    val decks = data.DeckPort.fromJson(obj)
    Dependent.firstFleet = extractFleetShips(decks)(1)
    Dependent.fleets = (1 to FleetMax).map(extractFleetShips(decks))
    if(decks.nonEmpty) {
      val message = decks.map(_.summary).mkString("\n")
      NormalPostable("/deckport", write(decks), 1, message) :: Nil
    } else Nil
  }


  private def extractFleetShips(decks: Iterable[data.DeckPort])(num: Int): List[Int] =
    decks.find(_.id == num).map(_.ships).getOrElse(Nil)}
