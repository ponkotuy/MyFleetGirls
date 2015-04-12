package com.ponkotuy.restype

import com.ponkotuy.parser.Query

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object Port extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$Api/api_port/port\\z".r

  override def postables(q: Query): Seq[Result] = {
    Ship2.postables(q) ++
        Material.postables(q) ++
        NDock.postables(q) ++
        DeckPort.postables(q) ++
        Basic.postables(q)
  }
}
