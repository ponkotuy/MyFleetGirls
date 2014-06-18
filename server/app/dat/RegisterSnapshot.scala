package dat

/**
 * Date: 14/06/16.
 */
case class RegisterSnapshot(userId: Long, password: String, title: String, comment: String, deckport: Int)

object RegisterSnapshot {
  def fromReq(req: Map[String, Seq[String]]): Option[RegisterSnapshot] = {
    val f = controllers.Common.reqHead(req)(_)
    for {
      userId <- f("userId")
      pass <- f("password")
      title <- f("title")
      comment <- f("comment")
      deckport <- f("deckport")
    } yield {
      RegisterSnapshot(userId.toLong, pass, title, comment, deckport.toInt)
    }
  }
}
