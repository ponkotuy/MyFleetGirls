package dat

/**
 * Date: 14/06/19.
 */
case class DeleteSnapshot(userId: Long, password: String, snapId: Long)

object DeleteSnapshot {
  def fromReq(req: Map[String, Seq[String]]): Option[DeleteSnapshot] = {
    val f = controllers.Common.reqHead(req)(_)
    for {
      userId <- f("userId")
      pass <- f("password")
      snapId <- f("snapId")
    } yield {
      DeleteSnapshot(userId.toLong, pass, snapId.toLong)
    }
  }
}
