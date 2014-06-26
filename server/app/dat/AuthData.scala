package dat

/**
 * Date: 14/06/27.
 */
trait AuthData {
  def userId: Long
  def password: String
}

case class AuthDataImpl(userId: Long, password: String) extends AuthData

object AuthDataImpl {
  import controllers.Common._
  def fromReq(req: Map[String, Seq[String]]): Option[AuthDataImpl] = {
    for {
      userId <- reqHead(req)("userId")
      pass <- reqHead(req)("password")
    } yield {
      AuthDataImpl(userId.toLong, pass)
    }
  }
}
