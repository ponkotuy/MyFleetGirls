package com.ponkotuy.restype

import com.ponkotuy.data
import com.ponkotuy.data.CreateShipAndDock
import com.ponkotuy.parser.Query
import org.json4s.native.Serialization.write

import scala.util.matching.Regex

/**
 * @author ponkotuy
 * Date: 15/04/12.
 */
case object KDock extends ResType {
  import ResType._

  override def regexp: Regex = s"\\A$GetMember/kdock\\z".r

  override def postables(q: Query): Seq[Result] = {
    val docks = data.KDock.fromJson(q.obj).filterNot(_.completeTime == 0)
    val message = docks.map(_.summary).mkString("\n")
    NormalPostable("/kdock", write(docks), 1, message) ::
        docks.flatMap { dock =>
          CreateShip.createShips.get(dock.id).map { cShip =>
            val dat = CreateShipAndDock(cShip, dock)
            CreateShip.createShips.remove(dock.id)
            NormalPostable("/createship", write(dat))
          }
        }
  }
}
