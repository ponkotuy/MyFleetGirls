package com.ponkotuy.restype

import com.ponkotuy.data.Ship
import com.ponkotuy.parser.Query
import org.json4s._
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
object Ship2 extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/ship2\\z".r

  override def postables(q: Query): Seq[Result] = postablesFromObj(q.obj)

  def postablesFromObj(obj: JValue): Seq[Result] = {
    val ship = Ship.fromJson(obj)
    NormalPostable("/ship", write(ship), ver = 2, s"所持艦娘数 -> ${ship.size}") :: Nil
  }
}
