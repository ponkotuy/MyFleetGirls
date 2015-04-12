package com.ponkotuy.restype

import com.ponkotuy.parser.Query

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object Deck extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/deck\\z".r

  override def postables(q: Query): Seq[Result] = DeckPort.postables(q)
}
