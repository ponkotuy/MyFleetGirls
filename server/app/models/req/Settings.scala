package models.req

/**
 *
 * @author ponkotuy
 * Date: 14/05/27.
 */
case class Settings(userId: Long, shipId: Int)

object Settings {
  def fromReq(req: Map[String, Seq[String]]): Option[Settings] = {
    import controllers.Common._
    for {
      userId <- reqHead(req)("userId")
      shipId <- reqHead(req)("shipId")
    } yield {
      Settings(userId.toLong, shipId.toInt)
    }
  }
}
