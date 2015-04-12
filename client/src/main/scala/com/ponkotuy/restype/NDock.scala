package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
object NDock extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/ndock\\z".r

  override def postables(q: Query): Seq[Result] = {
    val docks = data.NDock.fromJson(q.obj)
    val message =  docks.filterNot(_.shipId == 0).map(_.summary).mkString("\n")
    NormalPostable("/ndock", write(docks), 1, message) :: Nil
  }
}
