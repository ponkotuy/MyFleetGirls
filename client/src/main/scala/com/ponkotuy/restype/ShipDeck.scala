package com.ponkotuy.restype

import com.ponkotuy.parser.Query

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/05/28.
 */
object ShipDeck extends ResType {
  import ResType._
  override def regexp: Regex = s"\\A$GetMember/ship_deck\\z".r

  override def postables(q: Query): Seq[Result] = Ship3.postables(q)
}
