package models.req

/**
 * Date: 14/06/16.
 */
case class RegisterSnapshot(userId: Long, title: String, comment: String, deckport: Int)

object RegisterSnapshot {
  def fromReq(req: Map[String, Seq[String]]): Option[RegisterSnapshot] = {
    val f = controllers.Common.reqHead(req)(_)
    for {
      userId <- f("userId")
      title <- f("title")
      comment <- f("comment")
      deckport <- f("deckport")
    } yield {
      RegisterSnapshot(userId.toLong, title, comment, deckport.toInt)
    }
  }
}
