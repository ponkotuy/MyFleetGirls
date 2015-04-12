package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.JsonAST.JValue
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object Material extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/material\\z".r

  override def postables(q: Query): Seq[Result] = postablesFromObj(q.obj)

  def postablesFromObj(obj: JValue): Seq[Result] = {
    val material = data.Material.fromJson(obj)
    NormalPostable("/material", write(material), 1, material.summary) :: Nil
  }
}
