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
case object Ship3 extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/ship3\\z".r

  override def postables(q: Query): Seq[Result] = {
    val update = Ship.fromJson(q.obj \ "api_ship_data")
    if (update.isEmpty) Nil
    else {
      NormalPostable("/update_ship", write(update)) :: Nil
    }
  }
}
