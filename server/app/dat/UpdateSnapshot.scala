package dat

import controllers.Common

/**
 * Date: 14/11/02.
 */
case class UpdateSnapshot(userId: Long, snapId: Long, title: String, comment: String)

object UpdateSnapshot {
  def fromReq(req: Map[String, Seq[String]]): Option[UpdateSnapshot] = {
    val f = Common.reqHead(req)(_)
    for {
      userId <- f("userId")
      snapId <- f("snapId")
      title <- f("title")
      comment <- f("comment")
    } yield {
      UpdateSnapshot(userId.toLong, snapId.toLong, title, comment)
    }
  }
}
