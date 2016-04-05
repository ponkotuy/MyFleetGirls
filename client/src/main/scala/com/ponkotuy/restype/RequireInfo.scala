package com.ponkotuy.restype

import com.ponkotuy.parser.Query

import scala.util.matching.Regex

/**
  * Created by yosuke on 16/04/06.
  */
case object RequireInfo extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/require_info\\z".r

  override def postables(q: Query): Seq[Result] = {
    SlotItem.fromJson(q.obj \ "api_slot_item") ++ KDock.fromJson(q.obj \ "api_kdock")
  }
}
