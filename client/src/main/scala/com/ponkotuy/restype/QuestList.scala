package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object QuestList extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/questlist\\z".r

  override def postables(q: Query): Seq[Result] = {
    val qList = data.QuestList.fromJson(q.obj)
    if (qList.nonEmpty) {
      NormalPostable("/questlist", write(qList)) :: Nil
    } else Nil
  }
}
