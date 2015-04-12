package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object MapInfo extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/mapinfo\\z".r

  override def postables(q: Query): Seq[Result] = {
    val maps = data.MapInfo.fromJson(q.obj)
    NormalPostable("/mapinfo", write(maps)) :: Nil
  }
}
