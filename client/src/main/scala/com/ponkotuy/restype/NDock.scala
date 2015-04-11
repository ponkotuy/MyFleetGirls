package com.ponkotuy.restype

import org.json4s.JValue
import org.json4s.native.Serialization.write
import com.ponkotuy.data

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
object NDock extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/ndock\\z".r

  override def postables(req: Req, obj: JValue): Seq[Result] = {
    val docks = data.NDock.fromJson(obj)
    val message =  docks.filterNot(_.shipId == 0).map(_.summary).mkString("\n")
    NormalPostable("/ndock", write(docks), 1, message) :: Nil
  }
}
