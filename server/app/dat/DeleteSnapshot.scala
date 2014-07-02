package dat

/**
 * Date: 14/06/19.
 */
case class DeleteSnapshot(userId: Long, snapId: Long)

object DeleteSnapshot {
  def fromReq(req: Map[String, Seq[String]]): Option[DeleteSnapshot] = {
    val f = controllers.Common.reqHead(req)(_)
    for {
      userId <- f("userId")
      snapId <- f("snapId")
    } yield {
      DeleteSnapshot(userId.toLong, snapId.toLong)
    }
  }
}
